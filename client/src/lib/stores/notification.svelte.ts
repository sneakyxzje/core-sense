import { toast } from "svelte-sonner";

export interface Notification {
  title: string;
  message: string;
  link?: string;
  isRead: boolean;
  createdAt: string;
}

let list = $state<Notification[]>([]);
let unreadCount = $derived(list.filter((n) => !n.isRead).length);

export const notificationStore = {
  get list() {
    return list;
  },
  get unread() {
    return unreadCount;
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
  },

  markAllAsRead() {
    list = list.map((n) => ({ ...n, isRead: true }));
  },
};
