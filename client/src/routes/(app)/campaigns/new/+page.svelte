<script lang="ts">
  import FormBuilder from "@src/lib/components/FormBuilder.svelte";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import Input from "@src/lib/components/ui/input/input.svelte";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import {
    BrainCircuit,
    Edit,
    Info,
    ListChecks,
    Plus,
    Rocket,
    Save,
    Settings2,
  } from "lucide-svelte";
  import { createMutation, useQueryClient } from "@tanstack/svelte-query";
  import { createCampaign } from "@src/lib/api/campaign";
  import { goto } from "$app/navigation";
  import type { CreateCampaignRequest } from "@src/lib/types/CreateCampaignRequest";
  import Badge from "@src/lib/components/ui/badge/badge.svelte";
  import Separator from "@src/lib/components/ui/separator/separator.svelte";
  import ChevronRight from "@lucide/svelte/icons/chevron-right";

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
    console.log(formData);
  };
</script>

<div
  class="h-screen w-full flex flex-col bg-background overflow-hidden text-foreground"
>
  <header
    class="h-14 border-b bg-card/50 backdrop-blur-md flex items-center justify-between px-6 z-30 shrink-0"
  >
    <div class="flex items-center gap-4">
      <div class="flex items-center gap-2">
        <div
          class="w-8 h-8 rounded-lg bg-primary flex items-center justify-center shadow-lg shadow-primary/20"
        >
          <Rocket class="w-4 h-4 text-primary-foreground" />
        </div>
        <div class="h-4 w-[1px] bg-border mx-1"></div>
        <nav class="flex items-center gap-2 text-sm font-medium">
          <span
            class="text-muted-foreground hover:text-foreground cursor-pointer transition-colors"
            >Chiến dịch</span
          >
          <ChevronRight class="w-4 h-4 text-muted-foreground/50" />
          <span class="font-bold tracking-tight">Tạo chiến dịch mới</span>
        </nav>
      </div>
    </div>

    <div class="flex items-center gap-3">
      <div
        class="hidden md:flex items-center gap-2 px-3 py-1 rounded-full bg-secondary border border-border"
      >
        <div class="w-1.5 h-1.5 rounded-full bg-primary animate-pulse"></div>
        <span
          class="text-[10px] font-bold text-muted-foreground uppercase tracking-wider"
          >Draft Mode</span
        >
      </div>
      <Button
        variant="ghost"
        size="sm"
        class="text-xs font-semibold hover:bg-muted">Hủy bỏ</Button
      >
      <Button
        onclick={handleSubmit}
        size="sm"
        class="bg-primary hover:bg-primary/90 text-primary-foreground shadow-md px-6 font-bold transition-all active:scale-95"
      >
        <Save class="mr-2 h-4 w-4" /> Hoàn tất & Xuất bản
      </Button>
    </div>
  </header>

  <main class="flex-1 flex overflow-hidden">
    <aside
      class="w-[450px] border-r bg-card flex flex-col z-20 shrink-0 shadow-[1px_0_0_0_rgba(0,0,0,0.05)]"
    >
      <div class="flex-1 overflow-y-auto p-8 space-y-10 custom-scrollbar">
        <section class="space-y-6">
          <div class="flex items-center gap-2">
            <h3
              class="text-xs font-black uppercase tracking-[0.15em] text-muted-foreground"
            >
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
                class="border-border focus:border-primary focus:ring-4 focus:ring-primary/10 transition-all rounded-xl h-11 bg-muted/30 focus:bg-background"
                placeholder="VD: Khảo sát khách hàng 2024..."
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
                class="w-full rounded-xl border border-border bg-muted/30 focus:bg-background px-4 py-3 text-sm focus:outline-none focus:ring-4 focus:ring-primary/10 focus:border-primary transition-all resize-none placeholder:text-muted-foreground/60"
                placeholder="Nhập mô tả ngắn gọn giúp người dùng hiểu mục đích..."
                bind:value={formData.description}
              ></textarea>
            </div>
          </div>
        </section>

        <Separator />

        <section class="space-y-6 pb-10">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2 text-primary">
              <div
                class="p-1.5 rounded-md bg-primary text-primary-foreground shadow-md shadow-primary/20"
              >
                <BrainCircuit class="w-4 h-4" />
              </div>
              <h3 class="text-xs font-black uppercase tracking-[0.15em]">
                AI Prompt
              </h3>
            </div>
          </div>

          <div class="relative group">
            <textarea
              id="aiSystemPrompt"
              class="relative w-full min-h-[300px] rounded-2xl border border-border bg-background px-5 py-5 text-sm focus:outline-none focus:ring-0 focus:border-primary transition-all leading-relaxed shadow-sm italic text-foreground/80"
              placeholder="Nhập mong muốn chấm điểm của bạn..."
              bind:value={formData.aiSystemPrompt}
            ></textarea>

            <div
              class="mt-3 flex items-start gap-2.5 text-[11px] text-muted-foreground italic leading-relaxed px-1"
            >
              <Info class="w-3.5 h-3.5 mt-0.5 text-primary shrink-0" />
              AI sẽ dựa vào hướng dẫn này để phân tích câu trả lời của ứng viên ngay
              sau khi họ nộp bài.
            </div>
          </div>
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
              class="text-lg font-bold flex items-center gap-2 tracking-tight"
            >
              <ListChecks class="w-5 h-5 text-primary" />
              Thiết kế câu hỏi
            </h3>
            <p class="text-xs text-muted-foreground italic">
              Sắp xếp hiển thị nội dung câu hỏi
            </p>
          </div>
          <Badge
            variant="secondary"
            class="font-mono text-[10px] px-3 py-1 rounded-full border"
          >
            {formData.formSchema?.length || 0} Questions
          </Badge>
        </div>

        <div
          class="bg-background rounded-[24px] shadow-[0_20px_50px_rgba(0,0,0,0.04)] border border-border p-10 min-h-[calc(100vh-16rem)] transition-all"
        >
          <div class="w-full">
            <FormBuilder bind:schema={formData.formSchema} />
          </div>

          {#if !formData.formSchema?.length}
            <div
              class="mt-10 py-24 flex flex-col items-center justify-center border-2 border-dashed border-muted rounded-[20px] bg-muted/20 group hover:border-primary/50 transition-all"
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

        <div
          class="mt-16 opacity-20 hover:opacity-100 transition-all duration-500"
        >
          <details class="bg-slate-950 rounded-xl overflow-hidden shadow-2xl">
            <summary
              class="px-5 py-3 text-[10px] font-bold font-mono text-slate-500 cursor-pointer uppercase tracking-[0.2em] list-none flex items-center gap-2"
            >
              <div class="w-1.5 h-1.5 rounded-full bg-green-500"></div>
              Live JSON Schema
            </summary>
            <pre
              class="p-6 text-green-400 text-[11px] overflow-x-auto font-mono bg-slate-900/50">{JSON.stringify(
                formData,
                null,
                2
              )}</pre>
          </details>
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
