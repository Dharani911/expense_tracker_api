// src/state/authStore.js
import { login as apiLogin, register as apiRegister, logout as apiLogout } from "../api/auth";
import { getToken } from "../api/auth";

/**
 * Lightweight singleton auth store (no React hooks required)
 * Usage:
 *   import { authStore } from "../state/authStore";
 *   await authStore.login({ identifier, password });
 *   const { token, loading, error } = authStore.getState();
 *   const unsub = authStore.subscribe((state) => { ... });
 *   unsub();
 */

function createStore() {
  let state = {
    token: getToken(),
    loading: false,
    error: "",
    user: null, // optional: fill if your backend returns user details in AuthResponse
  };

  const subs = new Set();

  function set(partial) {
    state = { ...state, ...partial };
    subs.forEach((fn) => {
      try { fn(state); } catch {}
    });
  }

  function getState() {
    return state;
  }

  function subscribe(fn) {
    subs.add(fn);
    // emit current state immediately (optional)
    try { fn(state); } catch {}
    return () => subs.delete(fn);
  }

  // --- Actions ---

  async function login(credentials) {
    set({ loading: true, error: "" });
    try {
      // credentials can be {identifier, password} or (identifier, password) handled in api/auth.js
      const res = await apiLogin(credentials);
      // If your backend returns user info in AuthResponse, set it here:
      set({ loading: false, error: "", token: getToken(), user: res.user || null });
      return res;
    } catch (e) {
      set({ loading: false, error: e.message || "Login failed" });
      throw e;
    }
  }

  async function register(userPayload) {
    set({ loading: true, error: "" });
    try {
      const res = await apiRegister(userPayload);
      set({ loading: false, error: "", token: getToken(), user: res.user || null });
      return res;
    } catch (e) {
      set({ loading: false, error: e.message || "Registration failed" });
      throw e;
    }
  }

  async function logout() {
    set({ loading: true });
    try {
      await apiLogout();
    } finally {
      set({ loading: false, token: null, user: null });
    }
  }

  return { getState, subscribe, login, register, logout };
}

export const authStore = createStore();
