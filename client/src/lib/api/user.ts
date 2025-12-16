import type { User } from "@src/lib/types/user";
import { api, type Fetch } from "@src/lib/utils/api";

export const getMe = async (customFetch?: Fetch) => {
  const data = await api.get<User>("/user/me", customFetch);
  return data;
};
