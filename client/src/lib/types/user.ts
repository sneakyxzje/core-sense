export interface User {
  id: number;
  fullname: string;
  email: string;
  avatar?: string;
  role?: string;
  usedCount: number;
  planName: string;
  aiLimit: number;
  subscriptionId: string;
}
