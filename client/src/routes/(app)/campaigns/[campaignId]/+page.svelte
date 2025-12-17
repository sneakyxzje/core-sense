<script lang="ts">
  import * as Card from "$lib/components/ui/card";
  import { Badge } from "$lib/components/ui/badge";
  import { Button } from "$lib/components/ui/button";
  import { Input } from "$lib/components/ui/input";
  import { Label } from "$lib/components/ui/label";
  import { Separator } from "$lib/components/ui/separator";

  // Icons
  import {
    Calendar,
    Layers,
    Activity,
    Edit,
    Share2,
    BrainCircuit,
  } from "lucide-svelte";
  import { useGetCampaignById } from "@src/lib/queries/campaign.js";
  import Textarea from "@src/lib/components/ui/textarea/textarea.svelte";
  import { goto } from "$app/navigation";

  let { data } = $props();

  const campaign = useGetCampaignById(() => data.campaignId);
</script>

<div class="container max-w-5xl py-8 space-y-8">
  {#if campaign.isLoading}
    <div class="flex h-[50vh] items-center justify-center">
      <div class="flex flex-col items-center gap-2">
        <div
          class="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
        ></div>
        <p class="text-muted-foreground text-sm">Đang tải chiến dịch...</p>
      </div>
    </div>
  {:else if campaign.isError}
    <div class="rounded-lg bg-red-50 p-4 text-red-600 border border-red-200">
      <h3 class="font-bold">Không thể tải dữ liệu</h3>
      <p>{campaign.error?.message}</p>
    </div>
  {:else if campaign}
    <div class="flex flex-col md:flex-row justify-between items-start gap-4">
      <div>
        <div class="flex items-center gap-3 mb-2">
          <Badge
            variant={campaign?.data?.status === "ACTIVE"
              ? "default"
              : "secondary"}
            class="uppercase"
          >
            {campaign.status}
          </Badge>
          <span class="text-xs text-muted-foreground flex items-center gap-1">
            <Calendar class="w-3 h-3" />
            Tạo ngày {new Date(campaign.data!.createdAt).toLocaleDateString(
              "vi-VN"
            )}
          </span>
        </div>
        <h1 class="text-3xl font-bold tracking-tight">
          {campaign?.data?.name}
        </h1>
        <p class="text-muted-foreground mt-1 max-w-2xl">
          {campaign?.data?.description}
        </p>
      </div>

      <div class="flex gap-2">
        <Button variant="outline" size="sm">
          <Share2 class="w-4 h-4 mr-2" /> Chia sẻ
        </Button>
        <Button size="sm">
          <Edit class="w-4 h-4 mr-2" /> Chỉnh sửa
        </Button>
      </div>
    </div>

    <Separator />

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-2 space-y-6">
        <Card.Root>
          <Card.Header>
            <Card.Title class="flex items-center gap-2">
              Cấu trúc Form khảo sát
            </Card.Title>
            <Card.Description
              >Đây là những gì người dùng sẽ nhìn thấy.</Card.Description
            >
          </Card.Header>

          <Card.Content class="space-y-6">
            {#if campaign?.data?.formSchema && campaign?.data?.formSchema.length > 0}
              {#each campaign.data?.formSchema as q, index (index)}
                <div class="rounded-lg border p-4 transition-colors">
                  <div class="mb-2 flex items-center justify-between">
                    <Label class="text-base font-medium">
                      {q.label}
                      {#if q.required}<span class="text-red-500 ml-1">*</span
                        >{/if}
                    </Label>
                    <Badge variant="outline" class="text-[10px] h-5"
                      >{q.type}</Badge
                    >
                  </div>

                  {#if q.type === "textarea"}
                    <Textarea
                      disabled
                      placeholder={q.placeholder || "Nội dung trả lời..."}
                      class="bg-white"
                    />
                  {:else if q.type === "select"}
                    <select
                      disabled
                      class="flex h-10 w-full rounded-md border border-input bg-white px-3 py-2 text-sm opacity-50"
                    >
                      <option>-- Chọn đáp án --</option>
                      {#if q.options}
                        {#each q.options as opt}
                          <option>{opt}</option>
                        {/each}
                      {/if}
                    </select>
                  {:else}
                    <Input
                      disabled
                      type={q.type}
                      placeholder={q.placeholder || "Nội dung trả lời..."}
                      class="bg-white"
                    />
                  {/if}
                </div>
              {/each}
            {:else}
              <div class="text-center py-10 text-muted-foreground italic">
                Chưa có câu hỏi nào được thiết lập.
              </div>
            {/if}
          </Card.Content>
        </Card.Root>
      </div>

      <div class="space-y-6">
        <Card.Root class="border-indigo-100">
          <Card.Header>
            <Card.Title class="flex items-center gap-2 text-indigo-700">
              <BrainCircuit class="w-5 h-5" />
              Cấu hình AI
            </Card.Title>
          </Card.Header>
          <Card.Content>
            <Label
              class="text-xs font-semibold text-muted-foreground uppercase mb-2 block"
              >System Prompt</Label
            >
            <div
              class=" p-3 rounded-md border text-sm min-h-[100px] whitespace-pre-wrap"
            >
              {campaign.data?.aiSystemPrompt || "Chưa cấu hình prompt cho AI."}
            </div>
          </Card.Content>
        </Card.Root>

        <Card.Root>
          <Card.Header>
            <Card.Title class="flex items-center gap-2">
              <Activity class="w-5 h-5" />
              Thông tin hệ thống
            </Card.Title>
          </Card.Header>
          <Card.Content class="space-y-4 text-sm">
            <div class="flex justify-between py-2 border-b">
              <span class="text-muted-foreground">ID Chiến dịch</span>
              <code class=" px-1 py-0.5 rounded text-xs"
                >{campaign.data?.id.substring(0, 8)}...</code
              >
            </div>
            <div class="flex justify-between py-2 border-b">
              <span class="text-muted-foreground">Cập nhật lần cuối</span>
              <span
                >{new Date(campaign.data!.updatedAt).toLocaleDateString(
                  "vi-VN"
                )}</span
              >
            </div>
            <div class="flex justify-between py-2">
              <span class="text-muted-foreground">Tổng câu hỏi</span>
              <span class="font-bold"
                >{campaign.data?.formSchema?.length || 0}</span
              >
            </div>
            <div class="flex justify-between py-2">
              <span class="text-muted-foreground">Tổng câu trả lời:</span>
              {campaign.data?.totalSubmissions || "Không có"}
            </div>
            <Button
              onclick={() =>
                goto(`/campaigns/${campaign.data?.id}/submissions`)}
              class="flex cursor-pointer justify-between bg-primary py-2"
            >
              <span>Xem câu trả lời:</span>
            </Button>
          </Card.Content>
        </Card.Root>
      </div>
    </div>
  {/if}
</div>
