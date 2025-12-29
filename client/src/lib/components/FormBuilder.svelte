<script lang="ts">
  import type { FormQuestion } from "@src/lib/types/FormQuestion";
  import * as Card from "$lib/components/ui/card/index.js";
  import { dndzone, TRIGGERS, type DndEvent } from "svelte-dnd-action";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { Grip, GripVertical, Lock, Plus, Trash2 } from "lucide-svelte";
  import { flip } from "svelte/animate";
  import Input from "@src/lib/components/ui/input/input.svelte";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import X from "@lucide/svelte/icons/x";
  import Checkbox from "@src/lib/components/ui/checkbox/checkbox.svelte";
  let { schema = $bindable([]) } = $props<{ schema: FormQuestion[] }>();
  const SYSTEM_FIELDS = [
    {
      id: "sys-name",
      label: "Họ và tên",
      type: "text",
      required: true,
      isSystem: true,
      placeholder: "Nhập họ và tên ứng viên...",
    },
    {
      id: "sys-email",
      label: "Email",
      type: "email",
      required: true,
      isSystem: true,
      placeholder: "Nhập địa chỉ email...",
    },
  ];

  $effect(() => {
    if (schema.length === 0) {
      schema = [...SYSTEM_FIELDS];
    } else {
      const hasName = schema.some((q: FormQuestion) => q.id === "sys-name");
      const hasEmail = schema.some((q: FormQuestion) => q.id === "sys-email");

      if (!hasName || !hasEmail) {
        const customFields = schema.filter((q: FormQuestion) => !q.isSystem);
        schema = [...SYSTEM_FIELDS, ...customFields];
      }
    }
  });
  const addQuestion = () => {
    const newQ: FormQuestion = {
      id: crypto.randomUUID(),
      type: "text",
      label: "Câu hỏi mới",
      required: false,
      isSystem: false,
    };

    schema = [...schema, newQ];
  };
  const handleDnd = (e: any) => {
    const { items, info } = e.detail as DndEvent<FormQuestion>;
    schema = items;

    if (
      info.trigger === TRIGGERS.DRAGGED_ENTERED ||
      info.trigger === TRIGGERS.DROPPED_INTO_ANOTHER
    ) {
      dragDisabled = true;
    }

    const systemPart = items.filter((i) => i.isSystem);
    const customPart = items.filter((i) => !i.isSystem);
    schema = [
      ...systemPart.sort((a, b) => b.id.localeCompare(a.id)),
      ...customPart,
    ];
  };
  const removeQuestion = (id: string) => {
    schema = schema.filter((q: any) => q.id !== id);
  };
  let dragDisabled = $state(true);
  function startDrag() {
    dragDisabled = false;
  }
</script>

<div class="space-y-4">
  <div class="flex justify-end">
    <Button
      class="border border-base-border-1 text-base-1 bg-primary-1 hover:bg-primary-hover"
      onclick={addQuestion}
    >
      <Plus class="mr-2 h-4 w-4" />
      Thêm câu hỏi
    </Button>
  </div>

  <div
    use:dndzone={{
      items: schema,
      flipDurationMs: 300,
      dragDisabled: dragDisabled,
    }}
    onconsider={handleDnd}
    onfinalize={handleDnd}
    class="min-h-[200px] space-y-2 rounded-md p-4"
  >
    {#each schema as q (q.id)}
      <div animate:flip={{ duration: 300 }} class="relative group">
        {#if !q.isSystem}
          <button
            class="absolute left-2 top-3 z-10 cursor-grab active:cursor-grabbing p-1.5
               hover:bg-secondary transition-colors
               "
            onmousedown={startDrag}
            ontouchstart={startDrag}
            type="button"
          >
            <Grip class="h-4 w-4 text-muted-foreground" />
          </button>
        {/if}
        <Card.Root
          class=" group relative border-l border-base-border-1  transition-all"
        >
          <Card.Content class="p-4">
            <div class="flex gap-4 items-start">
              <div class="flex-1 space-y-4">
                <div class="flex gap-4">
                  <div class="flex-1">
                    <Label
                      >Tiêu đề câu hỏi {q.isSystem ? "(Bắt buộc)" : ""}</Label
                    >
                    <Input
                      bind:value={q.label}
                      placeholder="VD: Họ và tên..."
                      disabled={q.isSystem}
                      class="mt-1.5 border border-base-border-1 bg-base-2"
                    />
                  </div>

                  <div class="w-[180px]">
                    <Label>Loại dữ liệu</Label>
                    <select
                      bind:value={q.type}
                      onchange={() => {
                        if (q.type === "select" && !q.options) {
                          q.options = ["Lựa chọn 1", "Lựa chọn 2"];
                        }
                      }}
                      disabled={q.isSystem}
                      class="flex h-10 w-full mt-1.5 rounded-md border border-base-border-1 bg-base-2 px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                    >
                      <option value="text">Văn bản ngắn</option>
                      <option value="email">Email</option>
                      <option value="textarea">Văn bản dài</option>
                      <option value="number">Số</option>
                      <option value="select">Trắc nghiệm</option>
                    </select>
                  </div>
                </div>

                <div class="flex items-center justify-between pt-2">
                  <div class="flex items-center gap-2">
                    <Checkbox
                      id={`req-${q.id}`}
                      bind:checked={q.required}
                      disabled={q.isSystem}
                      class="h-4 w-4 border border-base-border-1 "
                    />
                    <Label
                      for={`req-${q.id}`}
                      class="cursor-pointer font-normal">Bắt buộc trả lời</Label
                    >
                  </div>

                  <Input
                    bind:value={q.placeholder}
                    placeholder="Gợi ý mờ (Placeholder)..."
                    disabled={q.isSystem}
                    class="max-w-[300px] border border-base-border-1 bg-base-2 h-8 text-sm"
                  />
                </div>

                {#if q.type === "select"}
                  <div class="mt-2 rounded-md p-3 border border-base-border-1">
                    <Label
                      class="mb-2 block text-xs font-semibold text-muted-foreground uppercase"
                      >Danh sách câu trả lời</Label
                    >

                    <div class="space-y-2">
                      {#each q.options || [] as _, i}
                        <div class="flex gap-2">
                          <div
                            class="mt-3 h-4 w-4 rounded-full border border-base-border-1 bg-base-2"
                          ></div>

                          <Input
                            bind:value={q.options[i]}
                            placeholder={`Lựa chọn ${i + 1}`}
                            class="h-9 border border-base-border-1 bg-base-2"
                          />

                          <Button
                            variant="ghost"
                            size="icon"
                            class="h-9 w-9 text-muted-foreground hover:text-red-500"
                            onclick={() => {
                              q.options.splice(i, 1);
                              q.options = q.options;
                            }}
                          >
                            <X class="h-4 w-4" />
                          </Button>
                        </div>
                      {/each}
                    </div>

                    <Button
                      variant="ghost"
                      size="sm"
                      class="mt-3 h-8 text-xs text-blue-600 hover:text-blue-700 hover:bg-primary-hover"
                      onclick={() => {
                        q.options = [...(q.options || []), ""];
                      }}
                    >
                      <Plus class="mr-1 h-3 w-3" />
                      Thêm tùy chọn
                    </Button>
                  </div>
                {/if}
              </div>

              {#if !q.isSystem}
                <Button
                  variant="ghost"
                  size="icon"
                  onclick={() => removeQuestion(q.id)}
                  class="text-red-500 hover:bg-red-50 hover:text-red-600"
                >
                  <Trash2 class="h-4 w-4" />
                </Button>
              {:else}
                <div class="p-2 text-muted-foreground/50">
                  <Lock class="h-4 w-4" />
                </div>
              {/if}
            </div>
          </Card.Content>
        </Card.Root>
      </div>
    {/each}
  </div>
</div>
