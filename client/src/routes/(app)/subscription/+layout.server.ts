import { api } from "@src/lib/utils/api";
import type { LayoutServerLoad } from "./$types";
import type { Subscription } from "@src/lib/types/subscription";

export const load: LayoutServerLoad = async ({ fetch }) => {
  const data = await api.get<Subscription[]>(`/subscriptions`, fetch);
  return {
    subscriptions: data,
  };
};
