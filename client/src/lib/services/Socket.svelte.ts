import { Client } from "@stomp/stompjs";

const submissionListeners = new Set<(data: any) => void>();
const notificationListeners = new Set<(data: any) => void>();
export const socketClient = new Client({
  brokerURL: "ws://localhost:8080/ws",
  debug: (str) => console.log("STOMP DEBUG: ", str),
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000,
});
export const socketStatus = $state({
  connected: false,
});
export const createSocketClient = () => {
  socketClient.onConnect = () => {
    console.log("Socket connected!");
    socketStatus.connected = true;
    socketClient?.subscribe("/topic/submissions", (message) => {
      const data = JSON.parse(message.body);
      submissionListeners.forEach((fn) => fn(data));
    });
    socketClient?.subscribe("/user/queue/notifications", (message) => {
      const data = JSON.parse(message.body);
      notificationListeners.forEach((fn) => fn(data));
    });
  };
  socketClient.onDisconnect = () => {
    socketStatus.connected = false;
    socketClient.deactivate();
  };
  return socketClient;
};

export const subscribeSubmissions = (fn: (data: any) => void) => {
  submissionListeners.add(fn);
  return () => submissionListeners.delete(fn);
};

export const subscribeNotifications = (fn: (data: any) => void) => {
  notificationListeners.add(fn);
  return () => notificationListeners.delete(fn);
};
