import type { FormQuestion } from "@src/lib/types/FormQuestion";

export interface CreateCampaignRequest {
  name: string;
  description: string;
  aiSystemPrompt: string;
  formSchema: FormQuestion[];
}
