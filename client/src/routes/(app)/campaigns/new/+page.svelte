<script lang="ts">
  import FormBuilder from "@src/lib/components/FormBuilder.svelte";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import Input from "@src/lib/components/ui/input/input.svelte";
  import Label from "@src/lib/components/ui/label/label.svelte";

  import { Eye, Info, ListChecks, Pencil, Plus } from "lucide-svelte";
  import { createMutation, useQueryClient } from "@tanstack/svelte-query";
  import { createCampaign } from "@src/lib/api/campaign";
  import { goto } from "$app/navigation";
  import { Textarea } from "@src/lib/components/ui/textarea";
  import type { CreateCampaignRequest } from "@src/lib/types/campaign";

  const queryClient = useQueryClient();
  let formData = $state<CreateCampaignRequest>({
    name: "",
    description: "",
    aiSystemPrompt: "",
    formSchema: [],
  });
  const mutation = createMutation(() => ({
    mutationFn: (payload: CreateCampaignRequest) => createCampaign(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["campaign"] });
      goto("/campaigns");
    },
    onError: (error: any) => {
      console.error(error);
    },
  }));

  const handleSubmit = () => {
    mutation.mutate(formData);
  };

  let viewMode = $state("builder");
</script>

<div
  class="h-screen w-full flex flex-col bg-base-3 border-base-border-1 border overflow-hidden"
>
  <main class="flex-1 flex overflow-hidden">
    <aside
      class=" border-r border-base-border-1 bg-base-3 flex flex-col z-20 shrink-0 shadow-[1px_0_0_0_rgba(0,0,0,0.05)]"
    >
      <div class="flex-1 overflow-y-auto p-8 space-y-10 custom-scrollbar">
        <section class="space-y-6">
          <div class="flex items-center gap-2">
            <h3 class="text-xs font-bold uppercase text-base-fg-1">
              Tạo chiến dịch mới
            </h3>
          </div>

          <div class="space-y-5">
            <div class="group space-y-2">
              <Label
                for="name"
                class="text-[13px] font-bold group-focus-within:text-primary transition-colors"
              >
                Tên chiến dịch <span class="text-destructive">*</span>
              </Label>
              <Input
                id="name"
                class="border border-base-border-2 focus:border-base-border-focus focus-visible:ring-0 focus-visible:ring-offset-0 transition-all rounded-xl h-11 bg-base-2"
                placeholder="VD: Khảo sát khách hàng 2024..."
                autocomplete="off"
                bind:value={formData.name}
              />
            </div>

            <div class="group space-y-2">
              <Label
                for="description"
                class="text-[13px] font-bold group-focus-within:text-primary transition-colors"
                >Mô tả mục tiêu</Label
              >
              <textarea
                id="description"
                rows="3"
                class="w-full rounded-xl border bg-base-2 border-base-border-2 focus:border-base-border-focus focus-visible:ring-0 focus-visible:ring-offset-0 outline-none transition-colors px-4 py-3 text-sm resize-none"
                placeholder="Nhập mô tả ngắn gọn giúp người dùng hiểu mục đích..."
                bind:value={formData.description}
              ></textarea>
            </div>
          </div>
        </section>
        <section class="space-y-6 pb-10">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2 text-primary">
              <h3 class="text-xs font-black uppercase tracking-[0.15em]">
                AI Requirements
              </h3>
            </div>
          </div>

          <div class="relative group">
            <textarea
              id="aiSystemPrompt"
              class="relative w-full min-h-[300px] rounded-xl border bg-base-2 border-base-border-2 focus:border-base-border-focus focus-visible:ring-0 focus-visible:ring-offset-0 outline-none transition-colors px-4 py-3 text-sm resize-none"
              placeholder="Nhập mong muốn chấm điểm của bạn..."
              bind:value={formData.aiSystemPrompt}
            ></textarea>

            <div
              class="mt-3 flex items-start gap-2.5 text-[11px] text-muted-foreground italic leading-relaxed px-1"
            >
              <Info class="w-3.5 h-3.5 mt-0.5 text-primary shrink-0" />
              AI sẽ dựa vào hướng dẫn này để phân tích câu trả lời của ứng viên ngay
              sau khi họ gửi đơn.
            </div>
          </div>
          <Button
            class="w-full bg-primary-1 text-base-1 uppercase hover:bg-primary-hover h-11 rounded-xl font-bold"
            onclick={handleSubmit}
            >Tạo chiến dịch
          </Button>
        </section>
      </div>
    </aside>

    <section
      class="flex-1 bg-muted/30 overflow-y-auto relative custom-scrollbar flex flex-col items-center"
    >
      <div
        class="absolute inset-0 opacity-[0.4] pointer-events-none [background-size:24px_24px]"
      ></div>

      <div class="relative w-full max-w-4xl py-12 px-8 z-10">
        <div class="flex items-center justify-between mb-8">
          <div class="space-y-1">
            <h3
              class="text-lg font-bold text-base-fg-1 flex items-center gap-2 tracking-tight"
            >
              {#if viewMode === "builder"}
                <ListChecks class="w-5 h-5 text-primary" />
                Thiết kế câu hỏi
              {:else}
                <Eye class="w-5 h-5 text-primary" />
                Xem trước Form
              {/if}
            </h3>
            <p class="text-xs text-muted-foreground italic">
              {viewMode === "builder"
                ? "Sắp xếp hiển thị nội dung câu hỏi"
                : "Giao diện hiển thị với người dùng cuối"}
            </p>
          </div>

          <div
            class="flex items-center gap-2 bg-base-2 p-1 rounded-lg border border-base-border-2 shadow-sm"
          >
            <button
              onclick={() => (viewMode = "builder")}
              class="flex items-center gap-2 px-3 py-1.5 text-xs font-bold rounded-md transition-all {viewMode ===
              'builder'
                ? 'bg-background text-primary shadow-sm bg-primary-3'
                : 'text-muted-foreground hover:text-foreground'}"
            >
              <Pencil class="w-3.5 h-3.5" />
              Builder
            </button>
            <button
              onclick={() => (viewMode = "preview")}
              class="flex items-center gap-2 px-3 py-1.5 text-xs font-bold rounded-md transition-all {viewMode ===
              'preview'
                ? 'bg-background text-primary shadow-sm bg-primary-3'
                : 'text-muted-foreground hover:text-foreground'}"
            >
              <Eye class="w-3.5 h-3.5" />
              Preview
            </button>
          </div>
        </div>

        <div
          class="bg-background rounded-[24px] shadow-[0_20px_50px_rgba(0,0,0,0.04)] border border-base-border-1 min-h-[calc(100vh-16rem)] transition-all overflow-hidden relative"
        >
          {#if viewMode === "builder"}
            <div class="p-10 w-full h-full">
              <FormBuilder bind:schema={formData.formSchema} />

              {#if !formData.formSchema?.length}
                <div
                  class=" flex flex-col items-center justify-center rounded-[20px] transition-all"
                >
                  <div
                    class="w-14 h-14 rounded-2xl bg-background border border-border shadow-sm flex items-center justify-center mb-4 group-hover:scale-110 transition-transform"
                  >
                    <Plus
                      class="w-6 h-6 text-muted-foreground group-hover:text-primary"
                    />
                  </div>
                  <p
                    class="text-sm font-bold text-muted-foreground group-hover:text-primary transition-colors"
                  >
                    Thêm câu hỏi đầu tiên để bắt đầu
                  </p>
                </div>
              {/if}
            </div>
          {:else}
            <div class="flex flex-col h-full">
              <div
                class="h-14 border-b border-base-border-2 flex items-center px-6 bg-base-2 justify-between"
              >
                <div class="flex items-center gap-2">
                  <div
                    class="ml-4 px-3 py-1 bg-base-2 rounded text-[10px] text-base-fg-1 border border-base-border-1 shadow-sm w-64 truncate"
                  >
                    Link appears here...
                  </div>
                </div>
                <div
                  class="text-[10px] font-mono text-muted-foreground uppercase tracking-wider"
                >
                  Live Preview
                </div>
              </div>

              <div class="flex-1 overflow-y-auto p-8 md:p-12 custom-scrollbar">
                <div class="max-w-2xl mx-auto space-y-8">
                  <div
                    class="space-y-4 text-center border-b border-base-border-1 pb-8"
                  >
                    <h1
                      class="text-3xl font-black text-base-fg-1 tracking-tight"
                    >
                      {formData.name || "Tiêu đề form"}
                    </h1>
                    <p
                      class="text-base-fg-4 text-sm leading-relaxed whitespace-pre-wrap"
                    >
                      {formData.description ||
                        "Mô tả chi tiết sẽ hiện ở đây..."}
                    </p>
                  </div>

                  {#if formData.formSchema && formData.formSchema.length > 0}
                    <div class="space-y-8">
                      {#each formData.formSchema as question, index}
                        <div
                          class="space-y-3 animate-in fade-in slide-in-from-bottom-4 duration-500"
                        >
                          <Label
                            class="block text-sm font-semibold text-slate-800"
                          >
                            <span class="text-base-fg-1">{index + 1}.</span>
                            <span class="text-base-fg-1"
                              >{question.label ||
                                "Câu hỏi chưa có tiêu đề"}</span
                            >
                            {#if question.required}<span
                                class="text-base-fg-1 ml-1">*</span
                              >{/if}
                          </Label>

                          {#if question.type === "text"}
                            <Input
                              type="text"
                              class="w-full px-3 py-2 border text-base-fg-1 border-base-border-1 rounded-lg  bg-base-2 focus:ring-2 ring-primary/20 outline-none transition-all"
                              placeholder="Nhập câu trả lời..."
                              disabled
                            />
                          {:else if question.type === "textarea"}
                            <Textarea
                              class="w-full px-3 py-2 border text-base-fg-1 border-base-border-1 rounded-lg bg-base-2 focus:ring-2 ring-primary/20 outline-none transition-all resize-none"
                              placeholder="Nhập câu trả lời..."
                              disabled
                            ></Textarea>
                          {:else if question.type === "number"}
                            <Input
                              type="text"
                              class="w-full px-3 py-2 border border-base-border-1 text-base-fg-1 rounded-lg  bg-base-2 focus:ring-2 ring-primary/20 outline-none transition-all"
                              placeholder="Nhập câu trả lời..."
                              disabled
                            />
                          {:else if question.type === "select"}
                            <select
                              class="w-full px-3 py-2 border border-base-border-1 text-base-fg-1 rounded-lg bg-base-2 focus:ring-2 ring-primary/20 outline-none transition-all"
                            >
                              <option>Chọn một tùy chọn...</option>
                            </select>
                          {:else if question.type === "email"}
                            <Input
                              type="email"
                              class="w-full px-3 py-2 border border-base-border-1 text-base-fg-1 rounded-lg  bg-base-2 focus:ring-2 ring-primary/20 outline-none transition-all"
                              placeholder="Nhập câu trả lời..."
                              disabled
                            />
                          {:else}
                            <div
                              class="p-3 bg-slate-50 rounded border border-dashed text-xs text-muted-foreground"
                            >
                              Input type: {question.type}
                            </div>
                          {/if}
                        </div>
                      {/each}

                      <div class="pt-6">
                        <button
                          class="w-full py-3 bg-slate-900 text-white font-bold rounded-xl shadow-lg shadow-slate-900/20 opacity-90 cursor-not-allowed"
                        >
                          Gửi câu trả lời
                        </button>
                      </div>
                    </div>
                  {:else}
                    <div
                      class="text-center py-10 text-muted-foreground text-sm italic"
                    >
                      Chưa có câu hỏi nào để hiển thị.
                    </div>
                  {/if}
                </div>
              </div>
            </div>
          {/if}
        </div>

        <div
          class="mt-16 opacity-20 hover:opacity-100 transition-all duration-500"
        >
          <details
            class="bg-slate-950 rounded-xl overflow-hidden shadow-2xl"
          ></details>
        </div>
      </div>
    </section>
  </main>
</div>

<style>
  .custom-scrollbar::-webkit-scrollbar {
    width: 5px;
  }
  .custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb {
    background: var(--border);
    border-radius: 10px;
  }
  .custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: var(--muted-foreground);
  }

  :global(body) {
    margin: 0;
    padding: 0;
    overflow: hidden;
  }

  :global(input),
  :global(textarea) {
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  }
</style>
