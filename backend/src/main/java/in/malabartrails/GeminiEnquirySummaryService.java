package in.malabartrails;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class GeminiEnquirySummaryService {
  public record Result(String status, String summary) {}
  private final String apiKey, model;
  private final ObjectMapper json;
  private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

  public GeminiEnquirySummaryService(@Value("${app.gemini-api-key:}") String apiKey,@Value("${app.gemini-model:gemini-2.5-flash}") String model,ObjectMapper json){this.apiKey=apiKey;this.model=model;this.json=json;}

  public Result summarize(String destination, LocalDate travelDate, int adults, int children, BigDecimal budget, String message){
    if(apiKey==null||apiKey.isBlank())return new Result("UNAVAILABLE",null);
    String prompt="You are a travel sales assistant. Write a concise, factual internal trip brief in 2 sentences. Do not invent details, do not use the traveller's name or contact information, and do not mention that you are an AI. " +
      "Destination: "+safe(destination)+". Travel date: "+travelDate+". Travellers: "+adults+" adults, "+children+" children. Budget INR: "+(budget==null?"not provided":budget)+". Traveller note: "+safe(message);
    try{
      var body=Map.of("contents",List.of(Map.of("parts",List.of(Map.of("text",prompt)))),"generationConfig",Map.of("temperature",0.2,"maxOutputTokens",140));
      var request=HttpRequest.newBuilder(URI.create("https://generativelanguage.googleapis.com/v1beta/models/"+model+":generateContent?key="+apiKey)).timeout(Duration.ofSeconds(18)).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))).build();
      var response=http.send(request,HttpResponse.BodyHandlers.ofString());
      if(response.statusCode()<200||response.statusCode()>=300)return new Result("FAILED",null);
      JsonNode root=json.readTree(response.body());String text=root.at("/candidates/0/content/parts/0/text").asText("").trim();
      return text.isBlank()?new Result("FAILED",null):new Result("READY",text);
    }catch(Exception ignored){return new Result("FAILED",null);}
  }
  public Result summarize(Enquiry e){return summarize(e.destination,e.travelDate,e.adults,e.children,e.budget,e.message);}
  private String safe(String value){return value==null||value.isBlank()?"not provided":value.replaceAll("[\\r\\n]+"," ").trim();}
}
