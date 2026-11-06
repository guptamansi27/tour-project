package in.malabartrails;

import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.gaos.models.interactions.CreateModelInteraction;
import com.google.genai.gaos.models.interactions.Interaction;
import com.google.genai.gaos.models.interactions.InteractionsInput;
import com.google.genai.gaos.models.interactions.Model;
import com.google.genai.gaos.models.operations.CreateInteractionRequestBody;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class GeminiEnquirySummaryService {
  public record Result(String status, String summary, String error) {}

  private static final Logger logger = Logger.getLogger(GeminiEnquirySummaryService.class.getName());
  private final String model;
  private final Client client;

  public GeminiEnquirySummaryService(@Value("${app.gemini-api-key:}") String apiKey,
      @Value("${app.gemini-model:gemini-3.8-flash}") String model) {
    this.model = model;
    this.client = apiKey == null || apiKey.isBlank() ? null : Client.builder().apiKey(apiKey).build();
  }

  public Result summarize(String destination, LocalDate travelDate, int adults, int children,
      BigDecimal budget, String message) {
    if (client == null)
      return new Result("UNAVAILABLE", null, "GEMINI_API_KEY is not configured for the backend.");

    String prompt = "You are a travel sales assistant. Write a concise, factual internal trip brief in 2 sentences. " +
        "Do not invent details, do not use the traveller's name or contact information, and do not mention that you are an AI. " +
        "Destination: " + safe(destination) + ". Travel date: " + travelDate + ". Travellers: " + adults +
        " adults, " + children + " children. Budget INR: " + (budget == null ? "not provided" : budget) +
        ". Traveller note: " + safe(message);
    try {
      CreateModelInteraction params = CreateModelInteraction.builder()
          .model(Model.of(model))
          .input(InteractionsInput.of(prompt))
          .build();

      Interaction interaction = client.interactions
          .create(CreateInteractionRequestBody.of(params))
          .interaction()
          .orElseThrow(() -> new IllegalStateException("Gemini response did not contain an interaction"));
      String text = interaction.outputText().orElse("").trim();
      if (text.isBlank()) {
        logger.warning("Gemini interaction returned empty output (" + model + ")");
        return new Result("FAILED", null, "Gemini returned an empty summary. Check the model response and try again.");
      }
      return new Result("READY", text, null);
    } catch (ApiException ex) {
      logger.log(Level.WARNING, "Gemini interaction failed with HTTP " + ex.code() + " (" + model + ")");
      return new Result("FAILED", null, explainStatus(ex.code()));
    } catch (RuntimeException ex) {
      logger.log(Level.WARNING, "Gemini interaction failed: " + ex.getClass().getSimpleName());
      return new Result("FAILED", null,
          "Gemini request failed (" + ex.getClass().getSimpleName() + "). Check backend network access and logs.");
    }
  }

  public Result summarize(Enquiry enquiry) {
    return summarize(enquiry.destination, enquiry.travelDate, enquiry.adults, enquiry.children,
        enquiry.budget, enquiry.message);
  }

  @PreDestroy
  public void closeClient() {
    if (client != null) client.close();
  }

  private String explainStatus(int status) {
    if (status == 400) return "Gemini rejected the request (HTTP 400). Check the model name and request format.";
    if (status == 401 || status == 403)
      return "Gemini rejected the API key or project access (HTTP " + status + "). Check the key and API access.";
    if (status == 404) return "Gemini model not found (HTTP 404). Check GEMINI_MODEL.";
    if (status == 429) return "Gemini quota or rate limit reached (HTTP 429). Check project quota and billing.";
    if (status >= 500) return "Gemini service error (HTTP " + status + "). Try again later.";
    return "Gemini request failed (HTTP " + status + "). Check backend logs.";
  }

  private String safe(String value) {
    return value == null || value.isBlank() ? "not provided" : value.replaceAll("[\\r\\n]+", " ").trim();
  }
}
