<script lang="ts">
  import Sidebar from "@src/lib/components/Sidebar.svelte";
  import * as Sidebars from "$lib/components/ui/sidebar/index.js";
  let { children } = $props();
</script>

<Sidebars.Provider>
  <Sidebar />
  <main class="w-full bg-base-2">
    {@render children()}
  </main>
</Sidebars.Provider>
