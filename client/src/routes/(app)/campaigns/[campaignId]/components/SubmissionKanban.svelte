<script lang="ts">
  import EditableHeader from "@src/routes/(app)/campaigns/[campaignId]/components/EditableHeader.svelte";
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
  import type { SubmissionWithStage } from "@src/lib/types/submission";
  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/page.svelte";
  import {
    socketClient,
    socketStatus,
  } from "@src/lib/services/Socket.svelte.js";
  import { page } from "$app/state";
  import Email from "@src/lib/components/Email.svelte";
  import type { Template } from "@src/lib/types/template";
  let dragSourceColumnId = $state<string | null>(null);
  const user = page.data.user;
  const flipDurationMs = 200;
  const kanbanState = useCampaignState();
  const handleDndConsider = (
    columnId: string,
    e: CustomEvent<DndEvent<SubmissionWithStage>>
  ) => {
    const { items, info } = e.detail;
    if (!dragSourceColumnId) {
      const movedItem = kanbanState.submissions.find((s) => s.id === info.id);
      dragSourceColumnId = movedItem?.stageId || null;
    }
    items.forEach((item) => {
      item.stageId = columnId;
    });

    updateSubmissionsLocal(items, columnId);
  };
  const stateCampaign = useCampaignState();
  $effect(() => {
    if (!socketStatus.connected) {
      return;
    }
    const subscription = socketClient.subscribe(
      `/topic/${user.id}/campaign/${stateCampaign.campaignId}`,
      (message) => {
        const data = JSON.parse(message.body);
        console.log(data.type);
        if (data.type === "AI_AUTOMATION_MOVE") {
          const targetSub = stateCampaign.submissions.find(
            (s) => s.id === data.submissionId
          );
          if (targetSub) {
            targetSub.stageId = data.toStageId;
            toast.success(data.message);
          } else {
            toast.error("Đã có lỗi xảy ra");
          }
        }
      }
    );
    return () => subscription.unsubscribe();
  });
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
          `/campaigns/${kanbanState.campaignId}/submissions/${info.id}/stage`,
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
    const otherSubmissions = kanbanState.submissions.filter(
      (s) => s.stageId !== columnId
    );
    kanbanState.submissions = [...otherSubmissions, ...newColumnItems];
  };

  let open = $state(false);
  let stageToDelete = $state<string | null>(null);
  let targetStageId = $state<string | null>(null);
  const availableStages = $derived(
    kanbanState.columns.filter((s) => s.id !== stageToDelete)
  );
  const handleDelete = (columnId: string) => {
    stageToDelete = columnId;
    if (kanbanState.submissions.some((s) => s.stageId === columnId)) {
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
        `/campaigns/${stateCampaign.campaignId}/stages/delete`,
        { stageToDelete: stageToDelete, targetStage: targetStage },
        fetch
      );

      open = false;
      kanbanState.columns = kanbanState.columns.filter(
        (c) => c.id !== stageToDelete
      );
      if (targetStage) {
        kanbanState.submissions = kanbanState.submissions.map((s) =>
          s.stageId === stageToDelete ? { ...s, stageId: targetStage } : s
        );
      }
    } catch (error) {
      toast.error("Đã có lỗi xảy ra, xin vui lòng thử lại sau");
    }
  };

  const handleStarred = async (submissionId: string) => {
    const index = kanbanState.submissions.findIndex(
      (s) => s.id === submissionId
    );
    if (index === -1) return;

    const oldStatus = kanbanState.submissions[index].starred;
    kanbanState.submissions[index].starred = !oldStatus;

    try {
      await api.patch(
        `/submissions/${submissionId}/star`,
        { starred: !oldStatus },
        fetch
      );
    } catch (error) {
      kanbanState.submissions[index].starred = oldStatus;
      toast.error("Lỗi cập nhật, vui lòng thử lại");
    }
  };

  const handleArchive = async (submissionId: string) => {
    try {
      await api.post(`/submissions/${submissionId}/archive`, fetch);
      kanbanState.submissions = kanbanState.submissions.filter(
        (s) => s.id !== submissionId
      );
      toast.success("Archive Submission Success!");
    } catch (error) {
      toast.error("Something wrong");
      console.log(error);
    }
  };

  let isEmailOpen = $state(false);
  let emailData = $state({
    submissionId: "",
    to: "",
    subject: "",
    body: "",
    slug: "",
  });
  let emailTemplate = $state<Template[]>();
  const showEmail = async (s: any) => {
    console.log(s);
    isEmailOpen = true;
    emailData.to = s.email;
    emailData.submissionId = s.id;
    try {
      const data = await api.get<Template[]>(`/emails`, fetch);
      emailTemplate = data;
      console.log(emailTemplate);
    } catch (error) {
      console.error(error);
    }
  };

  const handleSendmail = async (emailData: any) => {
    const payload = {
      submissionId: emailData.submissionId,
      subject: emailData.subject,
      customBody: emailData.body,
      templateSlug: emailData.slug,
      variables: {},
    };
    console.log(payload);
    try {
      await api.post(`/emails`, payload, fetch);

      toast.success("Yêu cầu gửi mail đã được ghi nhận!");

      isEmailOpen = false;
    } catch (error) {
      console.error(error);
    }
  };
</script>

<div
  class="custom-scrollbar flex h-full w-full gap-4 overflow-x-auto px-2 pb-2 items-start"
>
  {#each kanbanState.columns as column, i (column.id)}
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
          items: kanbanState.submissions.filter((s) => s.stageId === column.id),
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
        {#each kanbanState.submissions.filter((s) => s.stageId === column.id) as s (s.id)}
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
                    onclick={() => showEmail(s)}
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
                      goto(
                        `/campaigns/${kanbanState.campaign?.id}/submissions/${s.id}`
                      )}
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

{#if isEmailOpen}
  <div
    class="fixed bottom-[10px] right-10 w-[550px] bg-base-4 shadow-2xl rounded-t-md border border-base-border-1 flex flex-col z-50 animate-in slide-in-from-bottom duration-300"
  >
    <header
      class="bg-base-3 text-base-fg-1 p-4 border-b border-base-border-2 rounded-t-md flex justify-between items-center text-sm font-bold"
    >
      <span>THƯ MỚI</span>
      <button
        onclick={() => (isEmailOpen = false)}
        class="hover:text-red-400 text-lg">×</button
      >
    </header>

    <div class="p-4 space-y-1">
      <div
        class="flex items-baseline gap-2 py-2 border-b border-base-border-1 group transition-all duration-200"
      >
        <span class="text-gray-400 text-sm font-medium select-none min-w-fit"
          >To:</span
        >
        <input
          bind:value={emailData.to}
          class="flex-1 bg-transparent border border-base-border-1 outline-none text-sm text-base-fg-1 placeholder:text-base-fg-3 placeholder:font-normal"
          placeholder="recipients@example.com"
        />
      </div>

      <div
        class="flex items-baseline gap-2 py-2 border-b border-base-border-1 group transition-all duration-200"
      >
        <span class="text-gray-400 text-sm font-medium select-none min-w-fit"
          >Subject:</span
        >
        <input
          bind:value={emailData.subject}
          class="flex-1 bg-transparent border border-base-border-1 outline-none text-sm text-base-fg-1 placeholder:text-base-fg-3 placeholder:font-normal"
          placeholder="Tiêu đề thư mời..."
        />
      </div>

      <div
        class="flex items-baseline gap-2 py-2 border-b border-gray-100 group focus-within:border-blue-500 transition-all duration-200"
      >
        <span class="text-gray-400 text-sm font-medium select-none min-w-fit"
          >Template:</span
        >

        <select
          class="flex-1 bg-base-4 border border-base-border-1 outline-none text-sm text-base-fg-1 placeholder:text-base-fg-3 placeholder:font-normal"
          onchange={(e) => {
            const selected = emailTemplate?.find(
              (t) => t.slug === e.target?.value
            );
            if (selected) {
              emailData.subject = selected.subject;
              emailData.body = selected.customBody;
              emailData.slug = selected.slug;
            }
          }}
        >
          <option value="" class="text-gray-400"
            >-- Chọn mẫu thư để bắt đầu nhanh --</option
          >
          {#each emailTemplate as t}
            <option value={t.slug} class="text-base-fg-1">{t.name}</option>
          {/each}
        </select>
      </div>

      <div class="min-h-[250px] pt-2">
        <Email
          bind:content={emailData.body}
          placeholder="Viết nội dung thư mời..."
        />
      </div>
    </div>

    <footer
      class="p-4 flex justify-between items-center border-t border-gray-50"
    >
      <div class="flex items-center gap-2">
        <button
          class="bg-blue-600 text-white px-8 py-2 rounded-full font-bold hover:bg-blue-700 shadow-lg shadow-blue-100 transition-all active:scale-95"
          onclick={() => handleSendmail(emailData)}
        >
          Gửi
        </button>
      </div>
      <div class="flex space-x-4 text-gray-400 text-sm italic">
        <span class="text-[10px]">Cá nhân hóa với [[candidate_name]]</span>
      </div>
    </footer>
  </div>
{/if}

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
