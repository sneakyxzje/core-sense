import { api } from "@src/lib/utils/api";
import type { LayoutServerLoad } from "./$types";
import type { NotificationSummary } from "@src/lib/types/notifications";
export const load: LayoutServerLoad = async ({ fetch }) => {
  const data = await api.get<NotificationSummary>("/notifications", fetch);
  return {
    notifications: data,
  };
};
