// src/api/client.js
import axios from "axios";
import { getToken, clearToken } from "./auth";

const API_BASE = import.meta.env.VITE_API_URL || "/api";

export const api = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
  withCredentials: false,
});

// Attach JWT to every request
api.interceptors.request.use((config) => {
  const t = getToken?.();
  if (t) config.headers.Authorization = `Bearer ${t}`;
  return config;
});

// Centralized auth error handling
api.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error?.response?.status;
    if (status === 401 || status === 403) {
      try { clearToken?.(); } catch {}
      const here = typeof window !== "undefined" ? window.location.pathname : "";
      if (!/\/(login|register)/.test(here) && typeof window !== "undefined") {
        window.location.assign("/login");
      }
    }
    return Promise.reject(error);
  }
);
