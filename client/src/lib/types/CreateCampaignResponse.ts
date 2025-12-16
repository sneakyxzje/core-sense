// String id,
// String name,
// String description,
// CampaignStatus status,
// LocalDateTime createdAt

export interface CreateCampaignResponse {
  id: string;
  name: string;
  description: string;
  status: string;
  createdAt: string;
}
