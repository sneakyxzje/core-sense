import { api } from "@src/lib/utils/api";
import type { LayoutServerLoad } from "./$types";
import type { CampaignWithSubmission } from "@src/lib/types/campaign";

export const load: LayoutServerLoad = async ({ fetch, params, url }) => {
  const campaignId = params.campaignId;
  const page = url.searchParams.get("page") || "0";
  const size = url.searchParams.get("size") || "10";
  const search = url.searchParams.get("search") || "";
  const res = await api.get<CampaignWithSubmission>(
    `/campaigns/${campaignId}/submissions?page=${page}&size=${size}&search=${search}&sort=submittedAt,desc`,
    fetch
  );
  const campaignName = res.campaign.name;
  return {
    campaign: res.campaign,
    submissions: res.submissions,
    campaignName,
    campaignId,
  };
};
