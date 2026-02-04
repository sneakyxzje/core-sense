<script lang="ts">
  import * as Dialog from "$lib/components/ui/dialog/index.js";
  import { ArrowRight, Copy, Settings, Share2, Zap } from "lucide-svelte";
  import * as Card from "$lib/components/ui/card";
  import * as Collapsible from "$lib/components/ui/collapsible";
  import { useCampaignState } from "@src/routes/(app)/campaigns/[campaignId]/state/index.svelte";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import type { CampaignSetting } from "@src/lib/types/campaign";
  import Info from "@lucide/svelte/icons/info";
  import Checkbox from "@src/lib/components/ui/checkbox/checkbox.svelte";
  let { data } = $props();
  const states = useCampaignState();
  import * as Select from "$lib/components/ui/select";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import Input from "@src/lib/components/ui/input/input.svelte";
  import Check from "@lucide/svelte/icons/check";

  let draftSettings: CampaignSetting = $state({
    automations: [
      {
        campaignId: states.campaignId ?? "",
        eventCode: data.automations?.[0]?.eventCode ?? "AI_FILTER",
        fromStage: data.automations?.[0]?.fromStageId ?? "",
        toStage: data.automations?.[0]?.toStageId ?? "",
        status: data.automations?.[0]?.isActive ?? false,
      },
      {
        campaignId: states.campaignId ?? "",
        eventCode: data.automations?.[1]?.eventCode ?? "MAIL_AUTO",
        toStage: data.automations?.[1]?.toStageId ?? "",
        status: data.automations?.[1]?.isActive ?? false,
      },
    ],
  });

  const display = $derived({
    source:
      states.kanban.columns.find(
        (c) => c.id === draftSettings.automations[0].fromStage,
      )?.stageName ?? "Chọn cột",
    target:
      states.kanban.columns.find(
        (c) => c.id === draftSettings.automations[0].toStage,
      )?.stageName ?? "Chọn cột đích",
    mailTarget:
      states.kanban.columns.find(
        (c) => c.id === draftSettings.automations[1].toStage,
      )?.stageName ?? "Chọn cột đích",
    isInvalid:
      draftSettings.automations[0].status &&
      draftSettings.automations[0].fromStage ===
        draftSettings.automations[0].toStage,
  });
</script>

<Dialog.Root bind:open={states.isCampaignSettingOpen}>
  <Dialog.Content
    class="sm:max-w-[900px] [&>button:last-child]:hidden h-[80vh] bg-base-3 border-base-border-1 p-0 overflow-hidden flex flex-col gap-0"
  >
    <div
      class="px-6 py-4 bg-base-2/80 border-b border-base-border-1 flex justify-between items-center shrink-0 w-full"
    >
      <div class="text-left">
        <Dialog.Title class="text-xl font-bold flex items-center gap-2">
          Cài đặt chiến dịch
        </Dialog.Title>
        <p
          class="text-[10px] text-muted-foreground uppercase tracking-widest mt-1"
        >
          {states.campaign?.name || "Campaign Management"}
        </p>
      </div>
      <div class="flex items-center gap-2">
        <Button
          class="border border-base-border-1 hover:border-base-border-hover"
          size="sm"
          onclick={() => (states.isCampaignSettingOpen = false)}>Đóng</Button
        >
        <Button
          size="sm"
          onclick={() => states.onSaveSetting(draftSettings)}
          class="bg-primary-1 text-white hover:bg-primary-1/90"
        >
          Lưu thay đổi
        </Button>
      </div>
    </div>

    <div class="flex flex-1 overflow-hidden">
      <aside
        class="w-[220px] bg-base-2/50 border-r border-base-border-1 p-4 flex flex-col gap-1 h-full shrink-0"
      >
        <h4
          class="px-3 text-[10px] font-bold text-muted-foreground uppercase mb-2"
        >
          Cấu hình
        </h4>

        <button
          class="flex cursor-pointer items-center gap-3 px-3 py-2 rounded-md text-sm transition-all {states.activeTab ===
          'general'
            ? 'bg-primary-1 text-white shadow-sm'
            : 'hover:bg-base-2 text-base-fg-2'}"
          onclick={() => (states.activeTab = "general")}
        >
          <Settings class="size-4" /> Cài đặt chung
        </button>

        <button
          class="flex cursor-pointer items-center gap-3 px-3 py-2 rounded-md text-sm transition-all {states.activeTab ===
          'automation'
            ? 'bg-primary-1 text-white shadow-sm'
            : 'hover:bg-base-2 text-base-fg-2'}"
          onclick={() => (states.activeTab = "automation")}
        >
          <Zap class="size-4" /> Tự động hóa
        </button>

        <div class="my-2 border-t border-base-border-1"></div>

        <h4
          class="px-3 text-[10px] font-bold text-muted-foreground uppercase mb-2"
        >
          Kết nối
        </h4>

        <button
          class="flex items-center cursor-pointer gap-3 px-3 py-2 rounded-md text-sm transition-all {states.activeTab ===
          'sharing'
            ? 'bg-primary-1 text-white shadow-sm'
            : 'hover:bg-base-2 text-base-fg-2'}"
          onclick={() => (states.activeTab = "sharing")}
        >
          <Share2 class="size-4" /> Chia sẻ & Link
        </button>
      </aside>

      <main class="flex-1 overflow-y-auto p-4 custom-scrollbar bg-base-3">
        {#if states.activeTab === "general"}
          <div
            class="max-w-xl space-y-8 animate-in fade-in slide-in-from-bottom-2"
          >
            <section>
              <h3 class="text-lg font-semibold mb-4">
                Thông tin phỏng vấn mặc định
              </h3>
              <div class="space-y-4">
                <div class="space-y-2">
                  <label class="text-xs font-medium text-muted-foreground"
                    >Địa điểm / Link họp</label
                  >
                  <input
                    type="text"
                    class="w-full p-2.5 rounded-md bg-base-2 border border-base-border-2 text-sm focus:ring-1 ring-primary-1 outline-none transition-all"
                    placeholder="Nhập link Google Meet hoặc địa chỉ..."
                  />
                </div>
                <div class="space-y-2">
                  <label class="text-xs font-medium text-muted-foreground"
                    >Ghi chú mặc định</label
                  >
                  <textarea
                    rows="4"
                    class="w-full p-2.5 rounded-md bg-base-2 border border-base-border-2 text-sm resize-none outline-none focus:ring-1 ring-primary-1 transition-all"
                    placeholder="Ghi chú sẽ gửi cho ứng viên..."
                  ></textarea>
                </div>
              </div>
            </section>
          </div>
        {/if}

        {#if states.activeTab === "automation"}
          <div class="animate-in fade-in slide-in-from-right-4 space-y-2">
            <div>
              <h3 class="text-lg font-semibold mb-4">Tự động hoá Kanban</h3>
              <div
                class="flex items-start gap-2 p-3 rounded-md bg-blue-500/5 border border-blue-500/20"
              >
                <Info class="size-4 text-blue-500 mt-0.5" />
                <p class="text-[11px] text-base-fg-1 leading-relaxed">
                  <strong>Lưu ý:</strong> Khi kích hoạt, ứng viên sẽ tự động được
                  chuyển cột dựa trên kết quả đánh giá từ AI.
                </p>
              </div>
            </div>

            <div class="grid gap-4">
              <Card.Root
                class="overflow-hidden border-none shadow-none transition-all duration-300 "
              >
                <Collapsible.Root
                  bind:open={draftSettings.automations[0].status}
                >
                  <div class=" flex items-center justify-between">
                    <div class="flex items-center gap-4">
                      <div class="grid gap-0.5 text-left">
                        <h4 class="font-bold text-sm tracking-tight">
                          Tự động chuyển cột khi AI đánh giá
                        </h4>
                        <p class="text-[11px] text-base-fg-3">
                          Chuyển ứng viên vào cột chỉ định sau khi hoàn tất phân
                          tích
                        </p>
                      </div>
                    </div>
                    <div class="flex items-center gap-2 px-3 py-1.5">
                      <Checkbox
                        id="ai-auto"
                        bind:checked={draftSettings.automations[0].status}
                      />
                    </div>
                  </div>
                  <Collapsible.Content>
                    <div
                      class=" pt-2 border-t mt-4 border-base-border-1/30 bg-base-3/30 rounded-b-xl"
                    >
                      <div class="flex items-end gap-3 mt-4">
                        <div class="flex-1 space-y-2">
                          <div class="flex items-center gap-2 ml-1">
                            <span
                              class="text-[10px] font-bold text-muted-foreground uppercase tracking-widest"
                            >
                              Nguồn
                            </span>
                          </div>
                          <Select.Root
                            type="single"
                            bind:value={draftSettings.automations[0].fromStage}
                          >
                            <Select.Trigger
                              class="h-10 bg-base-2 border-base-border-2 w-full hover:border-base-border-hover transition-colors text-xs font-medium shadow-none "
                              >{display.source}</Select.Trigger
                            >
                            <Select.Content
                              class="bg-base-2 border-base-border-2"
                            >
                              {#each states.kanban.columns as stage}
                                <Select.Item value={stage.id} class="text-xs"
                                  >{stage.stageName}</Select.Item
                                >
                              {/each}
                            </Select.Content>
                          </Select.Root>
                        </div>

                        <div class="flex items-center justify-center h-10">
                          <div
                            class="p-1.5 rounded-full bg-base-3 border border-base-border-1 shadow-inner"
                          >
                            <ArrowRight class="size-3.5 text-primary-1/60" />
                          </div>
                        </div>

                        <div class="flex-1 space-y-2">
                          <div class="flex items-center gap-2 ml-1">
                            <span
                              class="text-[10px] font-bold uppercase tracking-widest"
                            >
                              Đích
                            </span>
                          </div>
                          <Select.Root
                            type="single"
                            bind:value={draftSettings.automations[0].toStage}
                          >
                            <Select.Trigger
                              class="h-10 bg-base-2 w-full border border-base-border-2 hover:border-base-border-hover transition-colors text-xs font-bold shadow-none "
                            >
                              {display.target}
                            </Select.Trigger>
                            <Select.Content
                              class="bg-base-2 border-base-border-2"
                            >
                              {#each states.kanban.columns as stage}
                                <Select.Item value={stage.id} class="text-xs"
                                  >{stage.stageName}</Select.Item
                                >
                              {/each}
                            </Select.Content>
                          </Select.Root>
                        </div>
                      </div>
                    </div>
                    {#if display.isInvalid}
                      <p
                        class="text-[10px] text-negative-1 text-center mt-[20px]"
                      >
                        Cột nguồn và đích không được trùng nhau!
                      </p>
                    {/if}
                  </Collapsible.Content>
                </Collapsible.Root>
              </Card.Root>
            </div>

            <div class="grid gap-4">
              <Card.Root
                class="overflow-hidden border-none shadow-none transition-all duration-300 "
              >
                <Collapsible.Root
                  bind:open={draftSettings.automations[1].status}
                >
                  <div class=" flex items-center justify-between">
                    <div class="flex items-center gap-4">
                      <div class="grid gap-0.5 text-left">
                        <h4
                          class="font-bold text-sm text-base-fg-1 tracking-tight"
                        >
                          Tự động gửi email
                        </h4>
                        <p class="text-[11px] text-base-fg-3">
                          Tự động gửi email khi ứng viên ở cột đích
                        </p>
                      </div>
                    </div>
                    <div class="flex items-center gap-2 px-3 py-1.5">
                      <Checkbox
                        id="mail-auto"
                        bind:checked={draftSettings.automations[1].status}
                      />
                    </div>
                  </div>
                  <Collapsible.Content>
                    <div
                      class=" pt-2 border-t mt-4 border-base-border-1/30 bg-base-3/30 rounded-b-xl"
                    >
                      <div class="flex items-end gap-3 mt-4">
                        <div class="flex-1 space-y-2">
                          <div class="flex-1 space-y-2">
                            <div class="flex items-center gap-2 ml-1">
                              <span
                                class="text-[10px] font-bold uppercase tracking-widest"
                              >
                                Đích
                              </span>
                            </div>
                            <Select.Root
                              type="single"
                              bind:value={draftSettings.automations[1].toStage}
                            >
                              <Select.Trigger
                                class="h-10 bg-base-2 w-full border border-base-border-2 hover:border-base-border-hover transition-colors text-xs font-bold shadow-none "
                              >
                                {display.mailTarget}
                              </Select.Trigger>
                              <Select.Content
                                class="bg-base-2 border-base-border-2"
                              >
                                {#each states.kanban.columns as stage}
                                  <Select.Item value={stage.id} class="text-xs"
                                    >{stage.stageName}</Select.Item
                                  >
                                {/each}
                              </Select.Content>
                            </Select.Root>
                          </div>
                        </div>
                      </div>
                    </div></Collapsible.Content
                  >
                </Collapsible.Root>
              </Card.Root>
            </div>
          </div>
        {/if}

        {#if states.activeTab === "sharing"}
          <div class="animate-in fade-in slide-in-from-bottom-2 space-y-6">
            <h3 class="text-lg font-semibold mb-4">Chia sẻ chiến dịch</h3>
            <div class="flex flex-col gap-3">
              <Label for="link" class="text-base-fg-2">Liên kết</Label>
              <div class="flex items-center gap-2">
                <Input
                  id="link"
                  readonly
                  value={states.sharedLink}
                  class="flex-1 bg-base-2 border-base-border-1 text-base-fg-1 focus-visible:ring-primary-1"
                />
                <Button
                  type="button"
                  size="icon"
                  variant="secondary"
                  class="shrink-0 border border-base-border-1 bg-base-2 hover:bg-base-3 text-base-fg-1"
                  onclick={states.copyLink}
                >
                  {#if states.copied}
                    <Check class="h-4 w-4 text-green-500" />
                  {:else}
                    <Copy class="h-4 w-4" />
                  {/if}
                </Button>
              </div>
            </div>
          </div>
        {/if}
      </main>
    </div>
  </Dialog.Content>
</Dialog.Root>
