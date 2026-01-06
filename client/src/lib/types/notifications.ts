export type Notifications = {
  id: number;
  title: string;
  message: string;
  link?: string;
  type: string;
  isRead: boolean;
  createdAt: string;
};

export type NotificationSummary = {
  notifications: PageResponse<Notifications>;
  unreadCount: number;
};
