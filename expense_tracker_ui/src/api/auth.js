// src/api/auth.js
import { api } from "./client";

const TOKEN_KEY = import.meta.env.VITE_TOKEN_KEY || "et_token";

export function saveToken(t){ try{ localStorage.setItem(TOKEN_KEY, t); }catch{} }
export function clearToken(){ try{ localStorage.removeItem(TOKEN_KEY); }catch{} }
export function getToken(){ try{ return localStorage.getItem(TOKEN_KEY); }catch{ return null; } }

function asMessage(e){
  const d = e?.response?.data;
  if (typeof d === "string") return d;
  if (d?.message) return d.message;
  if (d?.error) return d.error;
  return e?.message || "Request failed";
}

// Pull token from your AuthResponse shape (adjust if needed)
export function extractToken(data){
  if (!data || typeof data !== "object") return null;
  return data.token || data.accessToken || data.jwt || data.idToken || null;
}

export async function login(identifierOrObj, maybePassword){
  try{
    const payload = typeof identifierOrObj === "object"
      ? identifierOrObj
      : { identifier: identifierOrObj, password: maybePassword };

    const res = await api.post("/auth/login", payload);

    // Save token so the interceptor can authorize subsequent requests
    const token = extractToken(res.data);
    if (token) saveToken(token);

    return res.data;
  }catch(e){
    const err = new Error(asMessage(e));
    err.status = e?.response?.status;
    err.data   = e?.response?.data;
    throw err;
  }
}

export async function register({ username, name, email, password }){
  try{
    const res = await api.post("/auth/register", { username, name, email, password });
    // some backends also return a token on register — save if present
    const token = extractToken(res.data);
    if (token) saveToken(token);
    return res.data;
  }catch(e){
    const err = new Error(asMessage(e));
    err.status = e?.response?.status;
    err.data   = e?.response?.data;
    throw err;
  }
}

export async function logout(){
  try{ await api.post("/auth/logout"); }catch{}
  clearToken();
}
