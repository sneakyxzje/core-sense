<script lang="ts">
  import Sidebar from "@src/lib/components/Sidebar.svelte";
  import * as Sidebars from "$lib/components/ui/sidebar/index.js";
  import {
    createSocketClient,
    subscribeNotifications,
  } from "@src/lib/services/Socket.svelte.js";
  import { notificationStore } from "@src/lib/stores/notification.svelte";
  let { data, children } = $props();

  $effect(() => {
    const client = createSocketClient();
    client.activate();

    const unsub = subscribeNotifications((noti) => {
      notificationStore.add(noti);
    });
    return () => {
      unsub();
      client.deactivate();
    };
  });
  $effect(() => {
    const notifications = data.notifications;
    if (notifications) {
      notificationStore.init(data.notifications);
    }
  });
</script>

<Sidebars.Provider>
  <Sidebar />
  <main class="w-full bg-base-2">
    {@render children()}
  </main>
</Sidebars.Provider>
