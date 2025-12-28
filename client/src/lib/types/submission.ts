export type TotalSubmissions = {
  campaignId: string;
  campaignName: string;
  totalSubmissions: number;
  activeCampaigns: number;
  highQualityRatio: number;
};

export type SubmissionChart = {
  timePoint: string;
  value: number;
};

export type SubmissionEvent = {
  campaignName: string;
  submissionId: string;
  message: string;
  totalSubmissions: number;
  submittedAt: string;
};

export type SubmissionSummary = {
  campaignName: string;
  submittedAt: string;
}