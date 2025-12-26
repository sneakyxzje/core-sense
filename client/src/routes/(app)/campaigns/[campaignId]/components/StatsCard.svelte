<script lang="ts">
  let { campaign, submissions } = $props();
</script>

<div class="grid grid-cols-1 md:grid-cols-3 gap-4">
  <div
    class="p-4 rounded-md border border-base-border-1 bg-base-3 shadow-sm flex items-center justify-between"
  >
    <div class="space-y-1">
      <span
        class="text-xs text-muted-foreground font-medium uppercase tracking-wider"
        >Tổng phản hồi</span
      >
      <div class="text-2xl font-black">{campaign.totalSubmissions}</div>
    </div>
  </div>
  <div
    class="p-4 rounded-xl border border-base-border-1 bg-base-3 shadow-sm flex items-center justify-between"
  >
    <div class="space-y-1">
      <span
        class="text-xs text-muted-foreground font-medium uppercase tracking-wider"
        >Điểm AI TB</span
      >
      <div class="text-2xl font-black text-green-600">
        {(
          submissions.reduce(
            (acc: number, curr: number) => acc + (curr.score || 0),
            0
          ) / (submissions.length || 1)
        ).toFixed(1)}/10
      </div>
    </div>
  </div>
</div>
