// src/api/expenses.js
import { api } from "./client";

/** Lists current user's expenses */
export async function listExpenses() {
  const res = await api.get("/expenses/list");
  return res.data;
}

/** Get one expense by id (owned by current user) */
export async function getExpense(id) {
  const res = await api.get(`/expenses/get?id=${id}`);
  return res.data;
}

/** Create expense */
export async function createExpense(payload) {
  const res = await api.post("/expenses/create", payload);
  return res.data;
}

/** Update expense */
export async function updateExpense(id, payload) {
  const res = await api.post(`/expenses/update?id=${id}`, payload);
  return res.data;
}

/** Categories enum list (e.g. ["FOOD","TRAVEL",...]) */
export async function getCategories() {
  const res = await api.get("/expenses/categories");
  const arr = Array.isArray(res.data) ? res.data : [];
  return [...new Set(arr.map(String).map((s) => s.toUpperCase()))];
}
export async function deleteExpense(id) {
  await api.post(`/expenses/delete?id=${id}`);
}
