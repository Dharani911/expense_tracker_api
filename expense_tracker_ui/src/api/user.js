// src/api/user.js
import { api } from "./client";

export async function getMyProfile() {
  const res = await api.get("/user/profile");
  return res.data;
}

export async function updateMyProfile({ name, email }) {
  // matches UpdateUserRequest on backend
  const res = await api.post("/user/profile/update", { name, email });
  return res.data;
}

export async function deactivateMyAccount() {
  await api.post("/user/profile/deactivate");
}
