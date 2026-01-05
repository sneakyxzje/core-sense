import { Client } from "@stomp/stompjs";

const submissionListeners = new Set<(data: any) => void>();
const notificationListeners = new Set<(data: any) => void>();

export const createSocketClient = () => {
  const client = new Client({
    brokerURL: "ws://localhost:8080/ws",
    debug: (str) => console.log("STOMP DEBUG: ", str),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = () => {
    console.log("Socket connected!");
    client?.subscribe("/topic/submissions", (message) => {
      const data = JSON.parse(message.body);
      submissionListeners.forEach((fn) => fn(data));
    });
    client?.subscribe("/user/queue/notifications", (message) => {
      const data = JSON.parse(message.body);
      notificationListeners.forEach((fn) => fn(data));
    });
  };
  return client;
};

export const subscribeSubmissions = (fn: (data: any) => void) => {
  submissionListeners.add(fn);
  return () => submissionListeners.delete(fn);
};

export const subscribeNotifications = (fn: (data: any) => void) => {
  notificationListeners.add(fn);
  return () => notificationListeners.delete(fn);
};
