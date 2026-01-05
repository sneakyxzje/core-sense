<script lang="ts">
  import type {
    CampaignDetail,
    CampaignStage,
    SubmissionWithStage,
  } from "@src/lib/types/campaign";
  import EditableHeader from "@src/routes/(app)/campaigns/[campaignId]/components/EditableHeader.svelte";
  import {
    ArrowRightLeft,
    Bot,
    Download,
    Ellipsis,
    Eye,
    Mail,
    Pencil,
    Star,
    Trash2,
    Zap,
  } from "lucide-svelte";
  import { dndzone, type DndEvent } from "svelte-dnd-action";
  import { flip } from "svelte/animate";
  import { api } from "@src/lib/utils/api";
  import { toast } from "svelte-sonner";
  import KanbanCard from "@src/routes/(app)/campaigns/[campaignId]/components/KanbanCard.svelte";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu/index.js";
  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import * as ContextMenu from "$lib/components/ui/context-menu/index.js";
  import { goto } from "$app/navigation";

  let {
    submissions = $bindable(),
    campaign,
    columns = $bindable(),
  }: {
    submissions: SubmissionWithStage[];
    campaign: CampaignDetail;
    columns: CampaignStage[];
  } = $props();
  let dragSourceColumnId = $state<string | null>(null);
  const flipDurationMs = 200;

  const handleDndConsider = (
    columnId: string,
    e: CustomEvent<DndEvent<SubmissionWithStage>>
  ) => {
    const { items, info } = e.detail;
    if (!dragSourceColumnId) {
      const movedItem = submissions.find((s) => s.id === info.id);
      dragSourceColumnId = movedItem?.stageId || null;
    }
    items.forEach((item) => {
      item.stageId = columnId;
    });

    updateSubmissionsLocal(items, columnId);
  };

  const handleDndFinalize = async (
    columnId: string,
    e: CustomEvent<DndEvent<SubmissionWithStage>>
  ) => {
    const { items, info } = e.detail;

    items.forEach((item) => {
      item.stageId = columnId;
    });
    const isChangingColumn =
      dragSourceColumnId !== null && dragSourceColumnId !== columnId;
    updateSubmissionsLocal(items, columnId);
    if (isChangingColumn) {
      try {
        await api.patch(
          `/campaigns/stages/${info.id}/column`,
          {
            stageId: columnId,
          },
          fetch
        );
      } catch (error) {
        toast.error("Lỗi cập nhật server, đang hoàn tác...");
      }
    }
    dragSourceColumnId = null;
  };

  const updateSubmissionsLocal = (
    newColumnItems: SubmissionWithStage[],
    columnId: string
  ) => {
    const otherSubmissions = submissions.filter((s) => s.stageId !== columnId);
    submissions = [...otherSubmissions, ...newColumnItems];
  };

  let open = $state(false);
  let stageToDelete = $state<string | null>(null);
  let targetStageId = $state<string | null>(null);
  const availableStages = $derived(
    columns.filter((s) => s.id !== stageToDelete)
  );
  const handleDelete = (columnId: string) => {
    stageToDelete = columnId;
    if (submissions.some((s) => s.stageId === columnId)) {
      open = true;
    } else {
      executeDelete(columnId, null);
    }
  };

  const executeDelete = async (
    stageToDelete: string | null,
    targetStage: string | null
  ) => {
    console.log("toDelete", stageToDelete);
    console.log("target", targetStage);
    try {
      await api.delete(
        `/submission/stages/delete`,
        { stageToDelete: stageToDelete, targetStage: targetStage },
        fetch
      );

      open = false;
      columns = columns.filter((c) => c.id !== stageToDelete);
      if (targetStage) {
        submissions = submissions.map((s) =>
          s.stageId === stageToDelete ? { ...s, stageId: targetStage } : s
        );
      }
    } catch (error) {
      toast.error("Đã có lỗi xảy ra, xin vui lòng thử lại sau");
    }
  };

  const handleStarred = async (submissionId: string) => {
    const index = submissions.findIndex((s) => s.id === submissionId);
    if (index === -1) return;

    const oldStatus = submissions[index].starred;
    submissions[index].starred = !oldStatus;

    try {
      await api.patch(
        `/submission/${submissionId}/star`,
        { starred: !oldStatus },
        fetch
      );
    } catch (error) {
      submissions[index].starred = oldStatus;
      toast.error("Lỗi cập nhật, vui lòng thử lại");
    }
  };
</script>

<div
  class="custom-scrollbar flex h-full w-full gap-4 overflow-x-auto px-2 pb-2 items-start"
>
  {#each columns as column, i (column.id)}
    <div class="flex h-full w-[320px] min-w-[320px] flex-col flex-shrink-0">
      <div class="mb-2 px-1 flex items-center justify-between flex-none">
        <div class="flex items-center gap-2">
          <EditableHeader
            onSave={(newName) => {
              column.stageName = newName;
            }}
            {column}
            {campaign}
          />
        </div>
        <DropdownMenu.Root>
          <DropdownMenu.Trigger
            class="p-1 hover:bg-base-3 rounded-md transition-colors outline-none"
          >
            <Ellipsis class="w-4 h-4 text-muted-foreground cursor-pointer" />
          </DropdownMenu.Trigger>

          <DropdownMenu.Content
            align="end"
            class="w-48 bg-base-2 border-base-border-1"
          >
            <DropdownMenu.Group>
              <DropdownMenu.Item
                class="flex items-center gap-2 hover:bg-base-3 cursor-pointer py-2 px-3 text-sm"
              >
                <Zap class="w-3.5 h-3.5" />
                <span>Thao tác</span>
              </DropdownMenu.Item>

              <DropdownMenu.Item
                class="flex items-center gap-2  hover:bg-base-3 cursor-pointer py-2 px-3 text-sm"
              >
                <ArrowRightLeft class="w-3.5 h-3.5" />
                <span>Di chuyển cột</span>
              </DropdownMenu.Item>
            </DropdownMenu.Group>

            <DropdownMenu.Separator class="bg-base-border-1" />

            <DropdownMenu.Item
              class="flex items-center gap-2 cursor-pointer py-2 px-3 text-sm text-red-500 focus:text-red-500 focus:bg-red-500/10"
              onclick={() => handleDelete(column.id)}
            >
              <Trash2 class="w-3.5 h-3.5" />
              <span>Xóa cột</span>
            </DropdownMenu.Item>
          </DropdownMenu.Content>
        </DropdownMenu.Root>
      </div>

      <div
        class="custom-scrollbar flex-1 flex flex-col gap-3 p-3 border border-base-border-2 bg-base-2 rounded-xl overflow-y-auto shadow-sm min-h-[150px]"
        use:dndzone={{
          items: submissions.filter((s) => s.stageId === column.id),
          flipDurationMs,
          type: "submission",
          dropTargetStyle: {
            outline: "2px dashed var(--primary-1)",
            borderRadius: "12px",
          },
        }}
        onconsider={(e) => handleDndConsider(column.id, e)}
        onfinalize={(e) => handleDndFinalize(column.id, e)}
      >
        {#each submissions.filter((s) => s.stageId === column.id) as s (s.id)}
          <div animate:flip={{ duration: flipDurationMs }}>
            <ContextMenu.Root>
              <ContextMenu.Trigger>
                <KanbanCard {s} {campaign} />
              </ContextMenu.Trigger>

              <ContextMenu.Content class="w-64 bg-base-2 border-base-border-1">
                <ContextMenu.Group>
                  <ContextMenu.Label
                    class="text-[10px] uppercase tracking-wider opacity-50"
                    >AI Assistant</ContextMenu.Label
                  >
                  <ContextMenu.Item
                    class="gap-2 cursor-pointer hover:bg-base-border-hover"
                  >
                    <Bot />
                    <span>Tóm tắt nhanh bằng AI</span>
                  </ContextMenu.Item>
                </ContextMenu.Group>

                <ContextMenu.Separator class="bg-base-border-1" />

                <ContextMenu.Group>
                  <ContextMenu.Item
                    class="gap-2 cursor-pointer hover:bg-base-border-hover"
                  >
                    <Mail />
                    <span>Gửi email cho ứng viên</span>
                  </ContextMenu.Item>
                  <ContextMenu.Item
                    class="gap-2 cursor-pointer hover:bg-base-border-hover"
                    onclick={() => handleStarred(s.id)}
                  >
                    <Star
                      class="w-4 h-4 {s.starred
                        ? 'fill-yellow-400 text-yellow-400'
                        : ''}"
                    />
                    {s.starred ? "Bỏ đánh dấu ưu tiên" : "Đánh dấu ưu tiên"}
                  </ContextMenu.Item>
                </ContextMenu.Group>

                <ContextMenu.Separator class="bg-base-border-1" />

                <ContextMenu.Group>
                  <ContextMenu.Item
                    class="gap-2 cursor-pointer hover:bg-base-border-hover"
                    onclick={() =>
                      goto(`/campaigns/${campaign.id}/submissions/${s.id}`)}
                  >
                    <Eye />
                    <span>Xem chi tiết hồ sơ</span>
                  </ContextMenu.Item>
                  <ContextMenu.Item
                    class="gap-2 cursor-pointer hover:bg-base-border-hover"
                  >
                    <Download />
                    <span>Tải xuống CV</span>
                  </ContextMenu.Item>
                </ContextMenu.Group>

                <ContextMenu.Separator class="bg-base-border-1" />

                <ContextMenu.Item
                  class="gap-2 cursor-pointer text-red-500 focus:text-red-500 focus:bg-red-500/10"
                >
                  <Trash2 />
                  <span>Loại bỏ ứng viên</span>
                </ContextMenu.Item>
              </ContextMenu.Content>
            </ContextMenu.Root>
          </div>
        {:else}
          <div
            class="flex flex-col items-center justify-center py-10 opacity-40 pointer-events-none"
          >
            <p class="text-xs italic">Trống</p>
          </div>
        {/each}
      </div>
    </div>
  {/each}
  <Dialog.Root bind:open>
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
          bind:value={targetStageId}
          class="flex h-10 w-full rounded-md border-base-border-1 bg-base-2 px-3"
        >
          {#each availableStages as stage}
            <option value={stage.id}>{stage.stageName}</option>
          {/each}
        </select>
      </div>

      <Dialog.Footer>
        <Button class="border-base-border-2" onclick={() => (open = false)}
          >Hủy</Button
        >
        <Button
          class="bg-negative-1"
          disabled={!targetStageId}
          onclick={() => executeDelete(stageToDelete, targetStageId)}
        >
          Xác nhận di chuyển và xóa
        </Button>
      </Dialog.Footer>
    </Dialog.Content>
  </Dialog.Root>
</div>

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
