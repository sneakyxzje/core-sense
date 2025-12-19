<script lang="ts">
  import * as Card from "$lib/components/ui/card";
  import { Badge } from "$lib/components/ui/badge";
  import { Button } from "$lib/components/ui/button";
  import { Input } from "$lib/components/ui/input";
  import { Label } from "$lib/components/ui/label";
  import { Separator } from "$lib/components/ui/separator";

  import {
    Calendar,
    Activity,
    Edit,
    Share2,
    BrainCircuit,
    LinkIcon,
  } from "lucide-svelte";
  import Textarea from "@src/lib/components/ui/textarea/textarea.svelte";
  import { goto } from "$app/navigation";
  import Switch from "@src/lib/components/ui/switch/switch.svelte";
  import { api } from "@src/lib/utils/api.js";
  import * as HoverCard from "$lib/components/ui/hover-card/index.js";
  const formUrl = "http://localhost:5173/p/";
  let { data } = $props();
  let campaign = $state(data.campaign);
  $effect(() => {
    campaign = data.campaign;
  });
  let isToggleStatus = $state(false);
  let isLoading = $state(false);
  const handleCheckedChange = async (newValue: boolean) => {
    campaign.status = newValue ? "ACTIVE" : "INACTIVE";
    isLoading = true;
    try {
      await api.post(`/campaigns/${campaign.id}/toggle-status`, {
        enabled: newValue,
      });
    } catch (error) {
      isToggleStatus = !newValue;
      console.error(error);
    } finally {
      isLoading = false;
    }
  };
</script>

<div class="container max-w-8xl py-10 space-y-10">
  {#if campaign}
    <div class="flex flex-col md:flex-row justify-between items-start gap-6">
      <div class="space-y-3 flex-1">
        <div class="flex items-center gap-3">
          <Badge
            variant={campaign.status === "ACTIVE" ? "default" : "secondary"}
            class="px-3 py-1 uppercase tracking-wider text-[10px] flex items-center gap-2"
          >
            <span class="relative flex h-2 w-2">
              {#if campaign.status === "ACTIVE"}
                <span
                  class="animate-ping absolute inline-flex h-full w-full rounded-full bg-primary-foreground opacity-75"
                ></span>
              {/if}
              <span
                class="relative inline-flex rounded-full h-2 w-2 {campaign.status ===
                'ACTIVE'
                  ? 'bg-current'
                  : 'bg-muted-foreground'}"
              ></span>
            </span>
            {campaign.status}
          </Badge>

          <div class="h-4 w-[1px] bg-border"></div>

          <span
            class="text-xs text-muted-foreground flex items-center gap-1.5 font-medium"
          >
            <Calendar class="w-3.5 h-3.5" />
            Bắt đầu: {new Date(campaign.createdAt).toLocaleDateString("vi-VN")}
          </span>
        </div>

        <h1 class="text-4xl font-extrabold tracking-tight text-foreground">
          {campaign?.name}
        </h1>

        <p class="text-muted-foreground text-lg max-w-3xl leading-relaxed">
          {campaign?.description}
        </p>

        {#if campaign.status === "INACTIVE"}
          <div
            class="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-destructive/10 text-destructive border border-destructive/20 text-sm font-medium animate-in fade-in slide-in-from-left-2"
          >
            Chiến dịch hiện đang đóng. Sẽ không có phản hồi mới nào được ghi
            lại.
          </div>
        {/if}
      </div>

      <div class="flex gap-3 pt-2">
        <HoverCard.Root>
          <HoverCard.Trigger>
            {#snippet child({ props })}
              <Button
                {...props}
                variant="outline"
                class="shadow-sm cursor-pointer hover:bg-secondary transition-all"
              >
                <Share2 class="w-4 h-4 mr-2" /> Chia sẻ
              </Button>
            {/snippet}
          </HoverCard.Trigger>

          <HoverCard.Content class="w-80 p-4">
            <div class="flex flex-col gap-2">
              <div class="space-y-1">
                <h4
                  class="text-sm font-semibold flex items-center gap-1 text-foreground"
                >
                  <LinkIcon class="w-3 h-3" /> Đường dẫn khảo sát:
                </h4>
                <a
                  href={`${formUrl}${campaign.id}`}
                  target="_blank"
                  rel="noreferrer noopener"
                  class="text-sm text-primary underline underline-offset-4 break-all hover:opacity-80 transition-opacity font-mono block"
                >
                  {formUrl}{campaign.id}
                </a>
              </div>
              <p class="text-[10px] text-muted-foreground italic">
                * Nhấp vào đường dẫn để mở trang khảo sát trong tab mới.
              </p>
            </div>
          </HoverCard.Content>
        </HoverCard.Root>
        <Button class="shadow-md hover:shadow-lg transition-all">
          <Edit class="w-4 h-4 mr-2" /> Chỉnh sửa
        </Button>
      </div>
    </div>

    <Separator class="bg-border/60" />

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-10">
      <div class="lg:col-span-2 space-y-8">
        <Card.Root class="overflow-hidden border-2 shadow-sm">
          <Card.Header
            class=" border-b flex flex-row items-center justify-between py-6"
          >
            <div class="space-y-1">
              <Card.Title class="text-xl flex items-center gap-2">
                <Edit class="w-5 h-5 text-primary" />
                Cấu trúc Form khảo sát
              </Card.Title>
              <Card.Description
                >Giao diện hiển thị thực tế cho người dùng</Card.Description
              >
            </div>

            <div
              class="flex items-center gap-3 bg-background px-4 py-2 rounded-xl border shadow-sm"
            >
              <Label
                for="toggle-status"
                class="text-xs font-bold uppercase tracking-tighter opacity-70"
              >
                {campaign.status === "ACTIVE" ? "Mở" : "Khóa"}
              </Label>
              <Switch
                id="toggle-status"
                checked={campaign.status === "ACTIVE"}
                onCheckedChange={handleCheckedChange}
                disabled={isLoading}
                class="data-[state=checked]:bg-primary"
              />
            </div>
          </Card.Header>

          <Card.Content class="p-8 space-y-8">
            {#if campaign?.formSchema && campaign?.formSchema.length > 0}
              <div class="grid gap-8">
                {#each campaign.formSchema as q, index (index)}
                  <div class="group space-y-3 relative pb-2 transition-all">
                    <div class="flex items-center justify-between">
                      <Label
                        class="text-sm font-semibold text-foreground/80 flex items-center gap-2"
                      >
                        <span
                          class="flex items-center justify-center w-5 h-5 rounded bg-muted text-[10px] text-muted-foreground group-hover:bg-primary group-hover:text-primary-foreground transition-colors"
                        >
                          {index + 1}
                        </span>
                        {q.label}
                        {#if q.required}<span class="text-destructive">*</span
                          >{/if}
                      </Label>
                      <Badge
                        variant="secondary"
                        class="text-[9px] font-bold h-5 opacity-70 group-hover:opacity-100 uppercase transition-opacity"
                      >
                        {q.type}
                      </Badge>
                    </div>

                    {#if q.type === "textarea"}
                      <Textarea
                        disabled
                        placeholder={q.placeholder || "Nội dung trả lời..."}
                        class="bg-muted/20 border-dashed resize-none min-h-[100px]"
                      />
                    {:else if q.type === "select"}
                      <div class="relative">
                        <select
                          disabled
                          class="flex h-11 w-full rounded-md border border-input bg-muted/20 px-4 py-2 text-sm opacity-60 cursor-not-allowed appearance-none"
                        >
                          <option>-- Chọn đáp án --</option>
                          {#if q.options}
                            {#each q.options as opt}
                              <option>{opt}</option>
                            {/each}
                          {/if}
                        </select>
                      </div>
                    {:else}
                      <Input
                        disabled
                        type={q.type}
                        placeholder={q.placeholder || "Nội dung trả lời..."}
                        class="bg-muted/20 border-dashed h-11"
                      />
                    {/if}
                  </div>
                {/each}
              </div>
            {:else}
              <div
                class="text-center py-20 bg-muted/10 rounded-2xl border-2 border-dashed flex flex-col items-center gap-3"
              >
                <div class="p-4 bg-muted rounded-full">
                  <Edit class="w-8 h-8 text-muted-foreground/50" />
                </div>
                <p class="text-muted-foreground font-medium italic">
                  Chưa có câu hỏi nào được thiết lập.
                </p>
              </div>
            {/if}
          </Card.Content>
        </Card.Root>
      </div>

      <div class="space-y-8">
        <Card.Root class="border-indigo-100  overflow-hidden shadow-sm">
          <Card.Header>
            <Card.Title class="flex items-center gap-2 text-indigo-700 text-lg">
              <BrainCircuit class="w-5 h-5" />
              Cấu hình AI
            </Card.Title>
          </Card.Header>
          <Card.Content class="space-y-3">
            <Label
              class="text-[10px] font-bold text-indigo-400 uppercase tracking-widest block"
            >
              Prompt
            </Label>
            <div
              class="p-4 rounded-xl border border-indigo-100 bg-white/80 text-sm leading-relaxed text-indigo-900/80 min-h-[120px] shadow-inner italic whitespace-pre-wrap"
            >
              "{campaign.aiSystemPrompt || "Chưa cấu hình prompt cho AI."}"
            </div>
          </Card.Content>
        </Card.Root>

        <Card.Root class="shadow-sm">
          <Card.Header class="pb-2 border-b mb-4">
            <Card.Title class="flex items-center gap-2 text-lg">
              <Activity class="w-5 h-5 text-primary" />
              Thông tin hệ thống
            </Card.Title>
          </Card.Header>
          <Card.Content class="space-y-1">
            <div class="flex justify-between items-center py-3">
              <span class="text-sm text-muted-foreground">ID Chiến dịch</span>
              <code
                class="bg-muted px-2 py-1 rounded text-[10px] font-mono font-bold uppercase tracking-tight"
              >
                #{campaign.id.substring(0, 8)}
              </code>
            </div>
            <Separator class="opacity-50" />
            <div class="flex justify-between items-center py-3">
              <span class="text-sm text-muted-foreground">Cập nhật cuối</span>
              <span class="text-sm font-semibold"
                >{new Date(campaign.updatedAt).toLocaleDateString(
                  "vi-VN"
                )}</span
              >
            </div>
            <Separator class="opacity-50" />
            <div class="flex justify-between items-center py-3">
              <span class="text-sm text-muted-foreground">Tổng câu hỏi</span>
              <Badge variant="outline" class="font-bold"
                >{campaign.formSchema?.length || 0} mục</Badge
              >
            </div>

            <div class="pt-6 space-y-3">
              <div class="flex justify-between items-end mb-1">
                <span class="text-sm text-muted-foreground font-medium"
                  >Lượt phản hồi</span
                >
                <span class="text-3xl font-black tracking-tighter text-primary"
                  >{campaign.totalSubmissions || 0}</span
                >
              </div>

              <Button
                onclick={() => goto(`/campaigns/${campaign.id}/submissions`)}
                class="w-full h-11 cursor-pointer bg-primary shadow-lg hover:shadow-xl hover:-translate-y-0.5 transition-all flex justify-between px-6"
              >
                <span class="font-bold tracking-tight"
                  >Chi tiết câu trả lời</span
                >
                <Share2 class="w-4 h-4 rotate-90" />
              </Button>
            </div>
          </Card.Content>
        </Card.Root>
      </div>
    </div>
  {/if}
</div>
