<script lang="ts">
  import * as ContextMenu from "$lib/components/ui/context-menu/index.js";
  import EditableHeader from "@src/routes/(app)/campaigns/[campaignId]/components/EditableHeader.svelte";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu/index.js";
  import {
    ArrowRightLeft,
    Bot,
    Download,
    Ellipsis,
    Eye,
    Mail,
    Star,
    Trash2,
    Zap,
  } from "lucide-svelte";
  import KanbanCard from "@src/routes/(app)/campaigns/[campaignId]/components/KanbanCard.svelte";
  import type { CampaignStage } from "@src/lib/types/campaign";
  import { dndzone, type DndEvent } from "svelte-dnd-action";
  import type { Submission } from "@src/lib/types/submission";
  import { flip } from "svelte/animate";
  import { goto } from "$app/navigation";

  let {
    column,
    flipDurationMs,
    onConsider,
    onFinalize,
    items,
    onShowEmail,
    onDelete,
    onArchive,
    onStarred,
    campaignId,
    onShowBulk,
  }: {
    column: CampaignStage;
    flipDurationMs: number;
    onConsider: (colId: string, e: CustomEvent<DndEvent<Submission>>) => void;
    onFinalize: (colId: string, e: CustomEvent<DndEvent<Submission>>) => void;
    items: Submission[];
    onShowEmail: (s: Submission) => void;
    onDelete: (s: string) => void;
    onArchive: (s: string) => void;
    onStarred: (s: string) => void;
    campaignId: string | undefined;
    onShowBulk: () => void;
  } = $props();
  let internalItems = $state(items);
  let isDraggingInternal = $state(false);
  $effect(() => {
    if (!isDraggingInternal) {
      internalItems = items;
    }
  });
  const handleShowEmail = (s: Submission) => {
    onShowEmail(s);
  };
  const handleDelete = (s: string) => {
    onDelete(s);
  };
  const handleArchive = (s: string) => {
    onArchive(s);
  };
  const handleStarred = (s: string) => {
    onStarred(s);
  };
  const handleBulk = () => {
    onShowBulk();
  };
  const handleConsider = (e: CustomEvent<DndEvent<Submission>>) => {
    isDraggingInternal = true;
    const { items } = e.detail;
    internalItems = items;
    onConsider(column.id, e);
  };

  const handleFinalize = (e: CustomEvent<DndEvent<Submission>>) => {
    const { items } = e.detail;
    internalItems = items;
    onFinalize(column.id, e);
    isDraggingInternal = false;
  };
</script>

<div class="flex h-full w-[320px] min-w-[320px] flex-col flex-shrink-0">
  <div class="mb-2 px-1 flex items-center justify-between flex-none">
    <div class="flex items-center gap-2">
      <EditableHeader
        onSave={(newName) => {
          column.stageName = newName;
        }}
        {column}
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
            onclick={() => onShowBulk()}
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
      items: internalItems,
      flipDurationMs,
      type: "submission",
      dropTargetStyle: {
        outline: "2px dashed var(--primary-1)",
        borderRadius: "12px",
      },
    }}
    onconsider={handleConsider}
    onfinalize={handleFinalize}
  >
    {#each internalItems.filter((s) => s.stageId === column.id) as s (s.id)}
      <div animate:flip={{ duration: flipDurationMs }}>
        <ContextMenu.Root>
          <ContextMenu.Trigger>
            <KanbanCard {s} />
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
                onclick={() => handleShowEmail(s)}
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
                  goto(`/campaigns/${campaignId}/submissions/${s.id}`)}
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
              onclick={() => handleArchive(s.id)}
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
