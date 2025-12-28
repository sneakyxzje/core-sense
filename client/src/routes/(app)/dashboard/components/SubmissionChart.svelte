<script lang="ts">
  import * as Card from "$lib/components/ui/card";

  let { items } = $props<{ items: any[] }>();
</script>

<Card.Root class="col-span-4 border-base-border-1 bg-base-2/50 shadow-sm">
  <Card.Header>
    <Card.Title>Biểu đồ Submissions</Card.Title>
    <Card.Description>Số lượng phản hồi trong 7 ngày qua.</Card.Description>
  </Card.Header>
  <Card.Content class="pl-2">
    <div
      class="h-[350px] w-full flex items-end justify-between gap-2 p-4 pt-10"
    >
      {#each items as item}
        <div
          class="w-full flex flex-col justify-end gap-2 group cursor-pointer relative h-full"
        >
          <div
            class="opacity-0 group-hover:opacity-100 bg-info-1 transition-all duration-300 absolute bottom-[calc(var(--h)+45px)] left-1/2 -translate-x-1/2 text-white text-[10px] px-2 py-1 rounded shadow-xl z-20 whitespace-nowrap mb-2"
            style="--h: {item.heightPercentage}%"
          >
            {item.count} lượt nộp
            <div
              class="absolute -bottom-1 left-1/2 -translate-x-1/2 w-2 h-2 bg-primary-1 rotate-45"
            ></div>
          </div>

          <div
            class="bg-primary/20 bg-primary-1 w-full rounded-t-sm transition-all duration-500 group-hover:bg-primary/50 relative"
            style="height: {item.heightPercentage}%;"
          ></div>
          <div
            class="text-[10px] text-muted-foreground text-center truncate w-full"
          >
            {item.label}
          </div>
        </div>
      {/each}
    </div>
  </Card.Content>
</Card.Root>
