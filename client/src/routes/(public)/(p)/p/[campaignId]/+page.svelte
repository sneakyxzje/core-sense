<script lang="ts">
  import type { FormQuestion } from "@src/lib/types/FormQuestion";
  import { api } from "@src/lib/utils/api.js";
  let { data } = $props();
  let answers = $state<Record<string, any>>({});
  let isSubmitting = $state(false);
  let isSuccess = $state(false);
  async function handleSubmit(e: SubmitEvent) {
    e.preventDefault();
    console.log(answers);
    isSubmitting = true;
    const payload = {
      answers: answers,
    };
    try {
      const res = await api.post(`/submission/${data.campaignId}`, payload);
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
