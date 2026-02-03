<script lang="ts">
  import { MoveRight, X, ArrowRight } from "lucide-svelte";
  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/state/index.svelte";

  const states = useCampaignState();

  let selectedStageId = $state<string | null>(null);

  $effect(() => {
    if (states.submissions.isBulkMoveDialogOpen) {
      selectedStageId = null;
    }
  });
</script>

<Dialog.Root bind:open={states.submissions.isBulkMoveDialogOpen}>
  <Dialog.Content class="bg-base-3 border-base-border-1 p-0 overflow-hidden">
    <div class="px-6 py-4 bg-base-2/50 border-b border-base-border-1">
      <Dialog.Header>
        <div class="flex items-start gap-3">
          <div class="text-left">
            <Dialog.Title class="text-xl font-bold"
              >Di chuyển ứng viên</Dialog.Title
            >
            <p class="text-xs text-muted-foreground mt-1">
              Chọn cột đích cho {states.submissions.selectedCount} ứng viên
            </p>
          </div>
        </div>
      </Dialog.Header>
    </div>

    <div
      class="px-6 pt-2 pb-6 space-y-2 max-h-[60vh] overflow-y-auto custom-scrollbar"
    >
      <div class="space-y-2">
        <h3 class="text-sm font-semibold mb-3">Chọn cột đích:</h3>

        <div class="space-y-2">
          {#each states.kanban.columns as column (column.id)}
            <button
              class="w-full p-3 rounded-lg border-2 transition-all text-left
                     flex items-center justify-between group
                     {selectedStageId === column.id
                ? 'border-primary-1 bg-primary-1/10'
                : 'border-base-border-1 hover:border-base-border-2 bg-base-2'}"
              onclick={() => (selectedStageId = column.id)}
            >
              <div class="flex items-center gap-3">
                <div
                  class="w-2 h-8 rounded-full {selectedStageId === column.id
                    ? 'bg-primary-1'
                    : 'bg-base-border-2'}"
                ></div>
                <div>
                  <p class="font-medium text-sm">{column.stageName}</p>
                  <p class="text-xs text-muted-foreground">
                    {states.submissions.items.filter(
                      (s) => s.stageId === column.id,
                    ).length} ứng viên
                  </p>
                </div>
              </div>
            </button>
          {/each}
        </div>
      </div>
    </div>

    <div
      class="px-6 py-4 bg-base-2/30 border-t border-base-border-1 flex gap-2 justify-end"
    >
      <Button
        variant="ghost"
        onclick={() => states.submissions.closeBulkMoveDialog()}
      >
        <X class="w-3.5 h-3.5 mr-1" />
        Hủy
      </Button>

      <Button
        variant="default"
        class="bg-primary-1 text-white"
        disabled={!selectedStageId}
        onclick={() => {
          if (selectedStageId) {
            states.submissions.bulkMove(selectedStageId);
          }
        }}
      >
        <MoveRight class="w-3.5 h-3.5 mr-1" />
        Di chuyển
      </Button>
    </div>
  </Dialog.Content>
</Dialog.Root>
