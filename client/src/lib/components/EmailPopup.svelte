<script lang="ts">
  import Email from "@src/lib/components/Email.svelte";
  import type { EmailInteface } from "@src/lib/types/email";
  import type { Submission } from "@src/lib/types/submission";
  import type { Template } from "@src/lib/types/template";
  import { api } from "@src/lib/utils/api";
  import { toast } from "svelte-sonner";

  let {
    isEmailOpen = $bindable(false),
    selectedSubmission,
  }: {
    isEmailOpen?: boolean;
    selectedSubmission: Submission | undefined;
  } = $props();
  let emailTemplate = $state<Template[]>();
  let emailData = $state<EmailInteface>({
    to: "",
    subject: "",
    body: "",
    submissionId: "",
    slug: "",
    variables: {},
  });
  const fetchSlug = async () => {
    if (emailTemplate) return;
    try {
      const data = await api.get<Template[]>(`/emails`, fetch);
      emailTemplate = data;
    } catch (error) {
      console.error(error);
    }
  };
  $effect(() => {
    if (isEmailOpen && selectedSubmission) {
      fetchSlug();
      emailData.to = selectedSubmission.email;
      emailData.submissionId = selectedSubmission.id;
      emailData.subject = "";
      emailData.body = "";
      emailData.slug = "";
    }
  });
  const handleSendmail = async (emailData: EmailInteface) => {
    const payload = {
      submissionId: emailData.submissionId,
      subject: emailData.subject,
      customBody: emailData.body,
      templateSlug: emailData.slug,
      variables: emailData.variables,
    };
    console.log(payload);
    try {
      await api.post(`/emails`, payload, fetch);
      toast.success("Request send emaill success");
      isEmailOpen = false;
    } catch (error) {
      toast.error("Something went wrong!");
      console.error(error);
    }
  };
</script>

{#if isEmailOpen}
  <div
    class="fixed bottom-[10px] right-10 w-[550px] bg-base-4 shadow-2xl rounded-t-md border border-base-border-1 flex flex-col z-50 animate-in slide-in-from-bottom duration-300"
  >
    <header
      class="bg-base-3 text-base-fg-1 p-4 border-b border-base-border-2 rounded-t-md flex justify-between items-center text-sm font-bold"
    >
      <span>THƯ MỚI</span>
      <button
        onclick={() => (isEmailOpen = false)}
        class="hover:text-red-400 text-lg">×</button
      >
    </header>

    <div class="p-4 space-y-1">
      <div
        class="flex items-baseline gap-2 py-2 border-b border-base-border-1 group transition-all duration-200"
      >
        <span class="text-gray-400 text-sm font-medium select-none min-w-fit"
          >To:</span
        >
        <input
          bind:value={emailData.to}
          class="flex-1 bg-transparent border border-base-border-1 outline-none text-sm text-base-fg-1 placeholder:text-base-fg-3 placeholder:font-normal"
          placeholder="recipients@example.com"
        />
      </div>

      <div
        class="flex items-baseline gap-2 py-2 border-b border-base-border-1 group transition-all duration-200"
      >
        <span class="text-gray-400 text-sm font-medium select-none min-w-fit"
          >Subject:</span
        >
        <input
          bind:value={emailData.subject}
          class="flex-1 bg-transparent border border-base-border-1 outline-none text-sm text-base-fg-1 placeholder:text-base-fg-3 placeholder:font-normal"
          placeholder="Tiêu đề thư mời..."
        />
      </div>

      <div
        class="flex items-baseline gap-2 py-2 border-b border-gray-100 group focus-within:border-blue-500 transition-all duration-200"
      >
        <span class="text-gray-400 text-sm font-medium select-none min-w-fit"
          >Template:</span
        >

        <select
          class="flex-1 bg-base-4 border border-base-border-1 outline-none text-sm text-base-fg-1 placeholder:text-base-fg-3 placeholder:font-normal"
          onchange={(e) => {
            const selected = emailTemplate?.find(
              (t) => t.slug === (e.target as HTMLSelectElement)?.value,
            );
            if (selected) {
              emailData.subject = selected.subject;
              emailData.body = selected.customBody;
              emailData.slug = selected.slug;
            }
          }}
        >
          <option value="" class="text-gray-400"
            >-- Chọn mẫu thư để bắt đầu nhanh --</option
          >
          {#each emailTemplate as t}
            <option value={t.slug} class="text-base-fg-1">{t.name}</option>
          {/each}
        </select>
      </div>

      <div class="min-h-[250px] pt-2">
        <Email
          bind:content={emailData.body}
          placeholder="Viết nội dung thư mời..."
        />
      </div>
    </div>

    <footer
      class="p-4 flex justify-between items-center border-t border-base-border-1"
    >
      <div class="flex items-center gap-2">
        <button
          class="bg-blue-600 text-white px-8 py-2 rounded-full font-bold hover:bg-blue-700 shadow-lg cursor-pointer transition-all active:scale-95"
          onclick={() => handleSendmail(emailData)}
        >
          Gửi
        </button>
      </div>
    </footer>
  </div>
{/if}
