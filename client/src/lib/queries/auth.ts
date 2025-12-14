import { getMe } from "@src/lib/api/user";
import type { User } from "@src/lib/types/user";
import { createQuery } from "@tanstack/svelte-query";

export const authKeys = {
  all: ["auth"] as const,
  user: () => [...authKeys.all, "me"] as const,
};

export const useUser = () => {
  return createQuery<User>(() => ({
    queryKey: authKeys.user(),
    queryFn: getMe,
    staleTime: 1000 * 60 * 15,
    retry: 0,
  }));
};
