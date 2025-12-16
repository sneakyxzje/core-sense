import { getCampaignById, getCampaigns } from "@src/lib/api/campaign";
import { type CampaignDetail, type Campaign } from "@src/lib/types/campaign";
import { createQuery } from "@tanstack/svelte-query";

export const useCampaign = () => {
  return createQuery<Campaign[]>(() => ({
    queryKey: ["campaign", "list"],
    queryFn: getCampaigns,
    staleTime: 1000 * 60 * 15,
    retry: 10,
  }));
};

export const useGetCampaignById = (getId: () => string) => {
  return createQuery<CampaignDetail>(() => {
    const id = getId();

    return {
      queryKey: ["campaign", "detail", id],
      queryFn: () => getCampaignById(id),
      enabled: !!id,
      staleTime: 1000 * 60 * 15,
      retry: 3,
    };
  });
};
