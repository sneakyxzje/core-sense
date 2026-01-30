<script lang="ts">
  import { type DndEvent } from "svelte-dnd-action";

  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import Button from "@src/lib/components/ui/button/button.svelte";

  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/state/index.svelte";

  import EmailPopup from "@src/lib/components/EmailPopup.svelte";
  import KanbanColumn from "@src/routes/(app)/campaigns/[campaignId]/components/KanbanColumn.svelte";
  import type { Submission } from "@src/lib/types/submission";
  const flipDurationMs = 200;
  const globalState = useCampaignState();
  const onConsider = (colId: string, e: CustomEvent<DndEvent<Submission>>) =>
    globalState.kanban.onConsider(colId, e);
  const onFinalize = (colId: string, e: CustomEvent<DndEvent<Submission>>) =>
    globalState.kanban.onFinalize(colId, e);
  const campaignId = globalState.campaignId;
</script>

<div
  class="custom-scrollbar flex h-full w-full gap-4 overflow-x-auto px-2 pb-2 items-start"
>
  {#each globalState.kanban.columns as column, i (column.id)}
    <KanbanColumn
      {column}
      {flipDurationMs}
      {onConsider}
      {onFinalize}
      items={globalState.submissions.getSubmissionByColumn(column.id)}
      {campaignId}
      onShowEmail={(s) => globalState.submissions.showEmail(s)}
      onArchive={(s) => globalState.submissions.handleArchive(s)}
      onDelete={(s) => globalState.kanban.handleDelete(s)}
      onStarred={(s) => globalState.submissions.handleStarred(s)}
      onShowBulk={() => globalState.kanban.onShowBulk()}
    />
  {/each}
  <Dialog.Root bind:open={globalState.kanban.isDeletePopupOpen}>
    <Dialog.Content class="bg-base-3 border-base-border-1">
      <Dialog.Header>
        <Dialog.Title>Cột này đang có ứng viên!</Dialog.Title>
        <Dialog.Description>
          Bạn cần di chuyển ứng viên sang một cột khác trước khi xóa cột này.
          Hành động sẽ không thể hoàn tác.
        </Dialog.Description>
      </Dialog.Header>

      <div class="grid gap-4 py-4">
        <label for="target-stage">Chọn cột đích:</label>
        <select
          id="target-stage"
          bind:value={globalState.kanban.targetStageId}
          class="flex h-10 w-full rounded-md border-base-border-1 bg-base-2 px-3"
        >
          {#each globalState.kanban.availableStages as stage}
            <option value={stage.id}>{stage.stageName}</option>
          {/each}
        </select>
      </div>

      <Dialog.Footer>
        <Button
          class="border-base-border-2"
          onclick={() => (globalState.kanban.open = false)}>Hủy</Button
        >
        <Button
          class="bg-negative-1"
          disabled={!globalState.kanban.targetStageId}
          onclick={() =>
            globalState.kanban.deleteColumn(
              globalState.kanban.stageToDelete,
              globalState.kanban.targetStageId,
            )}
        >
          Xác nhận di chuyển và xóa
        </Button>
      </Dialog.Footer>
    </Dialog.Content>
  </Dialog.Root>
</div>

<EmailPopup
  bind:isEmailOpen={globalState.submissions.isEmailOpen}
  selectedSubmission={globalState.submissions.selectedSubmission}
/>

<style>
  .custom-scrollbar::-webkit-scrollbar {
    width: 6px;
    height: 8px;
  }
  .custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb {
    background-color: rgba(120, 120, 120, 0.3);
    border-radius: 10px;
  }
</style>
