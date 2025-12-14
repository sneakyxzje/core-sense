import type { User } from "@src/lib/types/user";
import { api } from "@src/lib/utils/api";

export const getMe = async () => {
  const data = await api.get<User>("/user/me");
  return data;
};
