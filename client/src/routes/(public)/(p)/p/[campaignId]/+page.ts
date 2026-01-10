import { api } from "@src/lib/utils/api";
import type { PageLoad } from "./$types";
import type { CampaignPublic } from "@src/lib/types/campaign";

export const load: PageLoad = async ({ params, fetch }) => {
  const id = params.campaignId;
  const res = await api.get<CampaignPublic>(
    `/submissions/campaign-schema/${id}`,
    fetch
  );

  if (!res.formSchema) {
    throw new Error("Không tìm thấy chiến dịch này hoặc link đã hết hạn!");
  }

  return {
    campaignId: id,
    campaignName: res.campaignName,
    formSchema: res.formSchema,
  };
};
