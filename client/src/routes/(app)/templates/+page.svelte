<script lang="ts">
  import { goto } from "$app/navigation";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { Plus } from "lucide-svelte";
  import type { PageData } from "./$types";
  import type { MarketTemplate } from "@src/lib/types/template";
  let { data }: { data: PageData } = $props();
  let iframeRef = $state<HTMLIFrameElement | null>(null);
  let selectedTemplates = $state<MarketTemplate | null>(null);
  $effect(() => {
    if (data.templates && data.templates.length > 0 && !selectedTemplates) {
      selectedTemplates = data.templates[0];
    }
  });
  $effect(() => {
    if (iframeRef && selectedTemplates) {
      const doc = iframeRef.contentDocument;
      doc?.open();
      doc?.writeln(selectedTemplates.content);
      doc?.close();
    }
  });

  console.log(data.templates);
</script>

<div class="space-y-6">
  <div class="flex items-center justify-between">
    <div>
      <h2 class="text-3xl font-bold tracking-tight">Templates</h2>
      <p class="text-base-fg-4 mt-1">
        Chọn mẫu hoặc tạo mới email công ty của bạn.
      </p>
    </div>
    <Button
      class="cursor-pointer border bg-primary-1 text-primary-fg-1 hover:bg-primary-hover "
      onclick={() => goto("/campaigns/new")}
    >
      <Plus class="mr-2 h-4 w-4" />
      Tạo mới
    </Button>
  </div>

  <div class="grid grid-cols-4 gap-4">
    <div class="col-span-1 space-y-2">
      {#each data.templates as item}
        <button
          class="w-full p-4 text-left border border-base-border-2 rounded-md cursor-pointer bg-base-3 hover:bg-base-4 transition-all
           {selectedTemplates?.id === item.id
            ? 'border-base-border-1 shadow-sm'
            : 'border-transparent'}"
          onclick={() => (selectedTemplates = item)}
        >
          <div class="font-semibold text-sm">{item.displayName}</div>
          <div class="text-xs text-base-fg-4 mt-1 line-clamp-1">
            {item.description}
          </div>
        </button>
      {/each}
    </div>
    <div class="col-span-3">
      <iframe
        bind:this={iframeRef}
        class="w-full h-full min-h-[400px] border border-base-border-1 rounded-md"
        title="Email Preview"
      ></iframe>
    </div>
  </div>
</div>
