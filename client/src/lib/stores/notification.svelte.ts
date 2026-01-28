import type {
  Notifications,
  NotificationSummary,
} from "@src/lib/types/notifications";
import { api } from "@src/lib/utils/api";
import { toast } from "svelte-sonner";

let list = $state<Notifications[]>([]);
let unreadCount = $state(0);
export const notificationStore = {
  get list() {
    return list;
  },
  get unreadCount() {
    return list.filter((n) => !n.isRead).length;
  },
  set unreadCount(v) {
    unreadCount = v;
  },

  init(data: NotificationSummary) {
    list = data.notifications.content;
    unreadCount = data.unreadCount;
  },

  add(noti: any) {
    list = [{ ...noti, isRead: false }, ...list];
    toast.info(noti.title, {
      description: noti.message,
      action: {
        label: "Xem",
        onClick: () => (window.location.href = noti.link),
      },
    });
    unreadCount += 1;
  },

  markAllAsRead() {
    list = list.map((n) => ({ ...n, isRead: true }));
  },

  async markAsRead(id: number) {
    const backupList = [...list];
    list = list.map((n) => (n.id === id ? { ...n, isRead: true } : n));

    unreadCount = list.filter((n) => !n.isRead).length;

    try {
      await api.patch(`/notifications/${id}/read`, {});
    } catch (error) {
      console.error("Error: ", error);
      list = backupList;
    }
  },
};
