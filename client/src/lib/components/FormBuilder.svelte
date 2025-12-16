<script lang="ts">
  import type { FormQuestion } from "@src/lib/types/FormQuestion";
  import * as Card from "$lib/components/ui/card/index.js";
  import { dndzone, type DndEvent } from "svelte-dnd-action";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { Plus, Trash2 } from "lucide-svelte";
  import { flip } from "svelte/animate";
  import Input from "@src/lib/components/ui/input/input.svelte";
  import Label from "@src/lib/components/ui/label/label.svelte";
  import X from "@lucide/svelte/icons/x";
  let { schema = $bindable([]) } = $props<{ schema: FormQuestion[] }>();
  const addQuestion = () => {
    const newQ: FormQuestion = {
      id: crypto.randomUUID(),
      type: "text",
      label: "Câu hỏi mới",
      required: false,
    };

    schema = [...schema, newQ];
  };
  const handleDnd = (e: CustomEvent<DndEvent<FormQuestion>>) => {
    schema = e.detail.items;
  };
  const removeQuestion = (id: string) => {
    schema = schema.filter((q: any) => q.id !== id);
  };
</script>

<div class="space-y-4">
  <div class="flex justify-end">
    <Button variant="outline" onclick={addQuestion}>
      <Plus class="mr-2 h-4 w-4" />
      Thêm câu hỏi
    </Button>
  </div>

  <div
    use:dndzone={{ items: schema, flipDurationMs: 300 }}
    onconsider={handleDnd}
    onfinalize={handleDnd}
    class="min-h-[200px] space-y-2 rounded-md border-2 border-dashed p-4"
  >
    {#each schema as q (q.id)}
      <div animate:flip={{ duration: 300 }}>
        <Card.Root
          class=" group relative border-l-4 border-l-transparent hover:border-l-primary transition-all"
        >
          <Card.Content class="p-4">
            <div class="flex gap-4 items-start">
              <div class="flex-1 space-y-4">
                <div class="flex gap-4">
                  <div class="flex-1">
                    <Label>Tiêu đề câu hỏi</Label>
                    <Input
                      bind:value={q.label}
                      placeholder="VD: Họ và tên..."
                      class="mt-1.5"
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
                      class="flex h-10 w-full mt-1.5 rounded-md border border-input bg-background px-3 py-2 text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                    >
                      <option value="text">Văn bản ngắn</option>
                      <option value="textarea">Văn bản dài</option>
                      <option value="number">Số</option>
                      <option value="select">Trắc nghiệm</option>
                    </select>
                  </div>
                </div>

                <div class="flex items-center justify-between pt-2">
                  <div class="flex items-center gap-2">
                    <input
                      type="checkbox"
                      id={`req-${q.id}`}
                      bind:checked={q.required}
                      class="h-4 w-4 accent-primary"
                    />
                    <Label
                      for={`req-${q.id}`}
                      class="cursor-pointer font-normal">Bắt buộc trả lời</Label
                    >
                  </div>

                  <Input
                    bind:value={q.placeholder}
                    placeholder="Gợi ý mờ (Placeholder)..."
                    class="max-w-[300px] h-8 text-sm"
                  />
                </div>

                {#if q.type === "select"}
                  <div class="mt-2 rounded-md p-3 border">
                    <Label
                      class="mb-2 block text-xs font-semibold text-muted-foreground uppercase"
                      >Danh sách đáp án</Label
                    >

                    <div class="space-y-2">
                      {#each q.options || [] as _, i}
                        <div class="flex gap-2">
                          <div
                            class="mt-3 h-4 w-4 rounded-full border border-slate-300"
                          ></div>

                          <Input
                            bind:value={q.options[i]}
                            placeholder={`Lựa chọn ${i + 1}`}
                            class="h-9 bg-white"
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
                      class="mt-3 h-8 text-xs text-blue-600 hover:text-blue-700 hover:bg-blue-50"
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

              <Button
                variant="ghost"
                size="icon"
                onclick={() => removeQuestion(q.id)}
                class="text-red-500 hover:bg-red-50 hover:text-red-600"
              >
                <Trash2 class="h-4 w-4" />
              </Button>
            </div>
          </Card.Content>
        </Card.Root>
      </div>
    {/each}
  </div>
</div>
