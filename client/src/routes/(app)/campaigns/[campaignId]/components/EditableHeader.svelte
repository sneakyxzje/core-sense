<script lang="ts">
  import type { CampaignDetail, CampaignStage } from "@src/lib/types/campaign";
  import { api } from "@src/lib/utils/api";
  import { toast } from "svelte-sonner";

  let {
    column = $bindable(),
    onSave,
  }: {
    column: CampaignStage;
    campaign: CampaignDetail;
    onSave: (newName: string) => void;
  } = $props();

  let isEditing = $state(false);
  let editedName = $state(column.stageName);
  const handleSave = async () => {
    const stageName = editedName.trim();
    if (stageName === "" || stageName === column.stageName) {
      editedName = column.stageName;
      isEditing = false;
      return;
    }
    try {
      await api.patch(
        `/campaigns/stages/${column.id}`,
        { stageName: stageName },
        fetch
      );

      onSave(stageName);
    } catch (error) {
      toast.error("Không thể lưu tên mới, thử lại sau nhé!");
      editedName = column.stageName;
    } finally {
      isEditing = false;
    }
  };

  const selectOnFocus = (node: HTMLInputElement) => {
    node.focus();
    node.select();
  };
</script>

<div class="flex items-center justify-between mb-2 px-1 group/header h-8">
  <div class="flex items-center flex-1 min-w-0">
    {#if isEditing}
      <input
        bind:value={editedName}
        class="h-7 w-full rounded border border-primary-1 bg-base-3 px-1 text-sm font-bold text-base-fg-1 focus:outline-none"
        onblur={handleSave}
        onkeydown={(e) => {
          if (e.key === "Enter") {
            handleSave();
          } else if (e.key === "Escape") {
            isEditing = false;
            editedName = column.stageName;
          }
        }}
        use:selectOnFocus
        use:selectOnFocus
      />
    {:else}
      <h3
        role="presentation"
        class="h-7 flex items-center font-bold text-sm text-base-fg-1 cursor-pointer transition-colors truncate w-full"
        onclick={() => (isEditing = true)}
      >
        {column.stageName}
      </h3>
    {/if}
  </div>
</div>
