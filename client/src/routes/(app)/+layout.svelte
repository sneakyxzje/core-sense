<script lang="ts">
  import Sidebar from "@src/lib/components/Sidebar.svelte";
  import * as Sidebars from "$lib/components/ui/sidebar/index.js";
  import { createSocketClient } from "@src/lib/services/Socket";
  import { notificationStore } from "@src/lib/stores/notification.svelte";
  let { children } = $props();

  $effect(() => {
    const client = createSocketClient(
      (sub) => console.log("Sub:", sub),
      (noti) => {
        notificationStore.add(noti);
      }
    );

    client.activate();
    return () => client.deactivate();
  });
</script>

<Sidebars.Provider>
  <Sidebar />
  <main class="w-full bg-base-2">
    {@render children()}
  </main>
</Sidebars.Provider>
