import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 5000,
  withCredentials: true
});

export async function listPackages(params = {}) {
  const { data } = await api.get('/packages', { params });
  return Array.isArray(data) ? data : data.content || [];
}

export async function readPackage(slug) {
  const { data } = await api.get(`/packages/${encodeURIComponent(slug)}`);
  return data;
}

export async function listDestinations() { const { data } = await api.get('/destinations'); return data; }
export async function readDestination(slug) { const { data } = await api.get(`/destinations/${encodeURIComponent(slug)}`); return data; }

export async function createEnquiry(payload) {
  return api.post('/enquiries', payload, { headers: { 'Idempotency-Key': crypto.randomUUID() } });
}
