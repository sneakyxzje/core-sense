import { Client } from "@stomp/stompjs";

export const createSocketClient = (
  onMessageReceived: (payload: any) => void,
  onNotificationReceived?: (payload: any) => void
) => {
  const client = new Client({
    brokerURL: "ws://localhost:8080/ws",
    debug: (str) => console.log("STOMP: " + str),

    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = () => {
    console.log("Connected to WebSocket server");

    client.subscribe("/topic/submissions", (message) => {
      if (message.body) {
        const data = JSON.parse(message.body);
        onMessageReceived(data);
      }
    });
    client.subscribe("/user/queue/notifications", (message) => {
      if (message.body) {
        const data = JSON.parse(message.body);
        onNotificationReceived?.(data);
      }
    });
  };

  client.onStompError = (frame) => {
    console.error("Broker reported error: " + frame.headers["message"]);
    console.error("Additional details: " + frame.body);
  };

  return client;
};
