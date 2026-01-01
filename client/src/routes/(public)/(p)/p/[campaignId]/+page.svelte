<script lang="ts">
  import type { FormQuestion } from "@src/lib/types/FormQuestion.js";
  import { api } from "@src/lib/utils/api.js";
  import { FileText, FileUp, X } from "lucide-svelte";
  let { data } = $props();
  let answers = $state<Record<string, any>>({});
  let isSubmitting = $state(false);
  let isSuccess = $state(false);
  let fileName = $state("");
  let fileInput: HTMLInputElement;
  function handleFileChange(e: Event, field: FormQuestion) {
    const target = e.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const file = target.files[0];
      fileName = file.name;
      answers[field.id] = file;
    }
  }
  async function handleSubmit(e: SubmitEvent) {
    e.preventDefault();
    const formData = new FormData();
    let fileToUpload: File | null = null;
    const cleanAnswers = { ...answers };
    const answersPayload = {
      sysName: answers["sys-name"],
      sysEmail: answers["sys-email"],
      allAnswers: { ...answers },
    };
    for (const key in answers) {
      if (answers[key] instanceof File) {
        fileToUpload = answers[key];
        delete answersPayload.allAnswers[key];
      }
    }
    if (fileToUpload) {
      formData.append("file", fileToUpload);
    } else {
      alert("Vui lòng tải lên CV!");
      return;
    }
    const dtoStructure = {
      answers: {
        sysName: cleanAnswers["sys-name"] || cleanAnswers["sysName"] || "",
        sysEmail: cleanAnswers["sys-email"] || cleanAnswers["sysEmail"] || "",
        allAnswers: cleanAnswers,
      },
    };

    console.log(answers);
    formData.append("data", JSON.stringify(dtoStructure));
    console.log(Array.from(formData.entries()));
    isSubmitting = true;
    try {
      const res = await api.post(`/submission/${data.campaignId}`, formData);
      if (res) isSuccess = true;
      else alert("Có lỗi xảy ra khi nộp đơn!");
    } finally {
      isSubmitting = false;
    }
  }
</script>

<div class="min-h-screen py-10 px-4">
  <div
    class="max-w-2xl mx-auto bg-card text-primary text-card-foreground shadow-md rounded-lg overflow-hidden border"
  >
    <div class="h-32 bg-indigo-600 flex items-end p-6">
      <h1 class="text-3xl font-bold text-white uppercase tracking-wider">
        {data.campaignName}
      </h1>
    </div>

    {#if isSuccess}
      <div class="p-10 text-center">
        <h2 class="text-2xl font-semibold">Cảm ơn bạn đã nộp đơn!</h2>
        <p class="text-gray-600 mt-2">
          Thông tin của bạn đã được ghi lại thành công.
        </p>
      </div>
    {:else}
      <form onsubmit={handleSubmit} class="p-8 space-y-6">
        <p class="text-gray-600 italic border-b pb-4">
          Vui lòng điền đầy đủ thông tin bên dưới.
        </p>

        {#each data.formSchema as field}
          <div class="space-y-2">
            <label class="block font-medium text-primary" for={field.id}>
              {field.label}
              {#if field.required}<span class="text-black">*</span>{/if}
            </label>

            {#if field.type === "text"}
              <input
                type="text"
                bind:value={answers[field.id]}
                required={field.required}
                placeholder={field.placeholder}
                class="w-full p-2 border text-black border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
              />
            {:else if field.type === "email"}
              <input
                bind:value={answers[field.id]}
                required={field.required}
                placeholder={field.placeholder}
                class="w-full p-2 border text-black border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
              />
            {:else if field.type === "textarea"}
              <textarea
                bind:value={answers[field.id]}
                required={field.required}
                rows="4"
                placeholder={field.placeholder}
                class="w-full p-2 border text-black border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
              ></textarea>
            {:else if field.type === "number"}
              <input
                bind:value={answers[field.id]}
                required={field.required}
                placeholder={field.placeholder}
                class="w-full p-2 border text-black border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
              />
            {:else if field.type === "files"}
              <div class="space-y-2">
                <div
                  class="relative group border-2 border-dashed border-gray-300 rounded-xl p-6 transition-all
           hover:border-indigo-500 hover:bg-indigo-50/30
           {fileName ? 'border-indigo-400 bg-indigo-50' : ''}"
                >
                  <input
                    type="file"
                    bind:this={fileInput}
                    accept=".pdf"
                    required={field.required}
                    onchange={(e) => handleFileChange(e, field)}
                    class="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10"
                  />

                  <div
                    class="flex flex-col items-center justify-center gap-2 text-center"
                  >
                    {#if !fileName}
                      <div
                        class="p-3 bg-gray-100 rounded-full group-hover:bg-indigo-100 transition-colors"
                      >
                        <FileUp
                          class="w-6 h-6 text-gray-500 group-hover:text-indigo-600"
                        />
                      </div>
                      <div>
                        <p class="text-sm font-semibold text-gray-700">
                          Nhấn để tải lên hoặc kéo thả CV
                        </p>
                        <p class="text-xs text-gray-500">
                          Hỗ trợ PDF (Tối đa 5MB)
                        </p>
                      </div>
                    {:else}
                      <div
                        class="flex items-center gap-3 p-2 bg-white rounded-lg shadow-sm border border-indigo-200 z-20"
                      >
                        <FileText class="w-8 h-8 text-indigo-500" />
                        <div class="text-left">
                          <p
                            class="text-sm font-medium text-gray-900 truncate max-w-[200px]"
                          >
                            {fileName}
                          </p>
                          <p class="text-[10px] text-gray-400 uppercase">
                            File PDF
                          </p>
                        </div>
                        <button
                          type="button"
                          class="p-1 hover:bg-red-100 rounded-full text-red-500 transition-colors"
                        >
                          <X class="w-4 h-4" />
                        </button>
                      </div>
                    {/if}
                  </div>
                </div>
              </div>
            {:else if field.type === "select"}
              <select
                bind:value={answers[field.id]}
                required={field.required}
                placeholder={field.placeholder}
                class="w-full p-2 border text-black border-gray-300 rounded focus:ring-2 focus:ring-indigo-500 outline-none"
              >
                <option value="">-- Chọn một phương án --</option>
                {#each field.options as opt}
                  <option value={opt}>{opt}</option>
                {/each}
              </select>
            {/if}
          </div>
        {/each}

        <div class="pt-6">
          <button
            type="submit"
            disabled={isSubmitting}
            class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 rounded-lg transition duration-200 disabled:opacity-50"
          >
            {isSubmitting ? "Đang gửi..." : "Gửi"}
          </button>
        </div>
      </form>
    {/if}
  </div>
</div>
