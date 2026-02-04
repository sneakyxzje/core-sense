import { api } from "@src/lib/utils/api";
import type { PageServerLoad } from "./$types";
import type { MarketTemplate } from "@src/lib/types/template";
export const load: PageServerLoad = async ({ fetch }) => {
  const data = await api.get<MarketTemplate[]>(`/emails/market`, fetch);
  return { templates: data };
};
