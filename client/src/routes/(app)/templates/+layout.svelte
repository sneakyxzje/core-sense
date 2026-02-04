<script>
  import BreadcrumbComponent from "@src/lib/components/BreadcrumbComponent.svelte";
  let { children } = $props();
</script>

<main
  class="mr-3 my-2 flex flex-col h-[calc(100svh-1rem)] rounded-lg border border-base-border-1 bg-base-2 overflow-hidden shadow-sm overflow-auto"
>
  <div class="border-base-border-1 flex-none border-b">
    <BreadcrumbComponent />
  </div>
  <div class="py-4 px-4 flex-1 overflow-y-auto">
    {@render children()}
  </div>
</main>
