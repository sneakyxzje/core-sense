<script lang="ts">
  import { CalendarDays, Download, MoveRight, Trash2, X } from "lucide-svelte";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/state/index.svelte";
  import { fly } from "svelte/transition";

  const state = useCampaignState();
</script>

{#if state.kanban.selectedCount > 0 && state.viewMode === "kanban"}
  <div
    class="fixed bottom-6 left-1/2 -translate-x-1/2 z-50"
    transition:fly={{ y: 100, duration: 300 }}
  >
    <div
      class="bg-primary-1 text-white rounded-md shadow-2xl
                px-6 py-3 flex items-center gap-4 backdrop-blur-sm"
    >
      <div class="flex items-center gap-2">
        <div
          class="w-7 h-7 rounded-full bg-base-4 text-base-fg-1 flex items-center justify-center"
        >
          <span class="text-sm font-bold">{state.kanban.selectedCount}</span>
        </div>
        <span class="text-sm font-medium">đã chọn</span>
      </div>

      <div class="w-px h-6 bg-white/30"></div>

      <Button
        variant="ghost"
        size="sm"
        class="text-white hover:bg-white/20 h-8 text-xs"
      >
        <CalendarDays class="w-4 h-4 mr-1.5" />
        Phỏng vấn
      </Button>

      <Button
        variant="ghost"
        size="sm"
        class="text-white hover:bg-white/20 h-8 text-xs"
        onclick={() => state.kanban.openBulkMoveDialog()}
      >
        <MoveRight class="w-4 h-4 mr-1.5" />
        Di chuyển
      </Button>

      <Button
        variant="ghost"
        size="sm"
        class="text-white hover:bg-white/20 h-8 text-xs"
        onclick={() => state.kanban.bulkArchive()}
      >
        <Trash2 class="w-4 h-4 mr-1.5" />
        Loại bỏ
      </Button>

      <div class="w-px h-6 bg-white/30"></div>

      <button
        class="w-7 h-7 rounded-full cursor-pointer hover:bg-white/20 flex items-center justify-center transition-colors"
        onclick={() => state.kanban.clearSelection()}
      >
        <X class="w-4 h-4" />
      </button>
    </div>
  </div>
{/if}
