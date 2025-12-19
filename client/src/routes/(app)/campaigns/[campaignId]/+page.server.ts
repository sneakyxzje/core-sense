import type { PageServerLoad } from "./$types";
import { api } from "@src/lib/utils/api";
import type { CampaignDetail } from "@src/lib/types/campaign";

export const load: PageServerLoad = async ({ fetch, params }) => {
  const campaignId = params.campaignId;
  const res = await api.get<CampaignDetail>(`/campaigns/${campaignId}`, fetch);
  return { campaign: res };
};
