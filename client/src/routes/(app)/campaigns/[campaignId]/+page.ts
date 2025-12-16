import { getCampaignById } from "@src/lib/api/campaign.js";
import type { PageLoad } from "./$types";

export const load: PageLoad = async ({ params, parent }) => {
  const { queryClient } = await parent();

  const campaignId = params.campaignId;

  await queryClient.prefetchQuery({
    queryKey: ["campaign", campaignId],
    queryFn: () => getCampaignById(campaignId),
  });

  return { campaignId };
};
