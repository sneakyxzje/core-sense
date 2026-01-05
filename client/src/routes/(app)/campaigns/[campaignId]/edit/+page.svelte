<script lang="ts">
  import { api } from "$lib/utils/api";
  import { goto, invalidateAll } from "$app/navigation";
  import FormBuilder from "$lib/components/FormBuilder.svelte";
  import { Button } from "$lib/components/ui/button";
  import { toast } from "svelte-sonner";

  let { data } = $props();

  let campaign = $state(JSON.parse(JSON.stringify(data.campaign)));
  let isSaving = $state(false);

  const onSave = async () => {
    isSaving = true;
    try {
      const ok = await api.put(`/campaigns/${campaign.id}`, campaign);
      if (ok) {
        await invalidateAll();
        toast.success("Bạn đã cập nhật chiến dịch thành công");
      }
      goto(`/campaigns/${campaign.id}`);
    } catch (err) {
      console.error(err);
    } finally {
      isSaving = false;
    }
  };

  $inspect(campaign);
</script>

<div class="p-8 max-w-4xl mx-auto bg-background space-y-6">
  <div class="flex justify-between items-center">
    <h1 class="text-xl font-bold">Chỉnh sửa: {data.campaign.name}</h1>
    <div class="flex gap-2">
      <Button variant="outline" onclick={() => history.back()}>Hủy</Button>
      <Button onclick={onSave} disabled={isSaving}>
        {isSaving ? "Đang lưu..." : "Lưu thay đổi"}
      </Button>
    </div>
  </div>

  <div class="grid gap-4 border p-4 rounded-lg">
    <div>
      <label class="text-sm font-medium">Tên chiến dịch</label>
      <input
        class="w-full border p-2 rounded bg-background"
        bind:value={campaign.name}
      />
    </div>

    <div>
      <label class="text-sm font-medium">Mô tả</label>
      <textarea
        class="w-full border p-2 rounded bg-background"
        bind:value={campaign.description}
      ></textarea>
    </div>

    <div>
      <label class="text-sm font-medium">AI Prompt</label>
      <textarea
        class="w-full border p-2 rounded bg-background h-32 font-mono"
        bind:value={campaign.aiSystemPrompt}
      ></textarea>
    </div>
  </div>

  <div class="border p-4 bg-background rounded-lg">
    <h2 class="font-bold mb-4">Câu hỏi khảo sát</h2>
    <FormBuilder bind:schema={campaign.formSchema} />
  </div>
</div>
