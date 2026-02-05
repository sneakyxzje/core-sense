<script lang="ts">
  import { api } from "@src/lib/utils/api.js";
  import { Check, Rocket, Zap, Crown, Building2 } from "lucide-svelte"; // Cần cài lucide-svelte

  let { data } = $props();
  const subscriptions = $state(data.subscriptions);

  const getPlanIcon = (id: string) => {
    if (id.toLowerCase().includes("pro")) return Crown;
    if (id.toLowerCase().includes("enterprise")) return Building2;
    return Zap;
  };
  const formatVND = (price: number) => {
    if (price === 0) return "Miễn phí";
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(price);
  };

  const handleUpgrade = async (subId: string) => {
    console.log(subId);
    try {
      const res = await api.post<any, any>(
        `/payment`,
        {
          subscriptionId: subId,
        },
        fetch,
      );

      if (res && res.data) {
        window.location.href = res.data;
      }
    } catch (error) {
      console.error(error);
    }
  };
</script>

<div class=" bg-base-2 px-4 flex flex-col space-y-6">
  <div class="max-w-4xl mx-auto text-center">
    <h1
      class="text-5xl md:text-6xl font-black text-base-fg-1 tracking-tighter leading-tight"
    >
      Nâng tầm hiệu suất tuyển dụng <br />
      <span class="relative inline-block mt-2">
        <span class="relative z-10 text-primary-1">với sức mạnh AI</span>
        <span class="absolute bottom-2 left-0 w-full h-4 bg-blue-100 -z-0"
        ></span>
      </span>
    </h1>

    <p
      class="text-xl text-gray-500 font-medium max-w-2xl mx-auto leading-relaxed"
    >
      Đơn giản hóa quy trình sàng lọc ứng viên, trích xuất thông tin thông minh
      và tìm ra những nhân tài phù hợp nhất chỉ trong vài giây.
    </p>
  </div>

  <div
    class="max-w-7xl mx-auto grid mt-[20px] grid-cols-1 md:grid-cols-3 gap-8"
  >
    {#each subscriptions as sub (sub.id)}
      {@const Icon = getPlanIcon(sub.id)}
      {@const isPro = sub.id.toLowerCase().includes("pro")}
      {@const isFree = sub.id.toLowerCase().includes("free")}
      <div
        class="relative flex flex-col p-8 bg-base-3 border {isPro
          ? 'border-base-border-2 ring-2 ring-blue-600 ring-opacity-50'
          : 'border-base-border-3'} rounded-2xl shadow-sm transition-all hover:shadow-xl"
      >
        {#if isPro}
          <div
            class="absolute top-0 right-8 -translate-y-1/2 bg-blue-600 text-white px-4 py-1 rounded-full text-sm font-bold uppercase tracking-wide"
          >
            Phổ biến nhất
          </div>
        {/if}

        <div class="mb-8">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-2xl font-bold text-base-fg-1">{sub.name}</h3>
            <div class="p-2 bg-blue-50 rounded-lg">
              <Icon class="w-6 h-6 text-blue-600" />
            </div>
          </div>
          <p class="text-base-fg-4 min-h-[3rem]">{sub.description}</p>
          <div class="mt-6 flex items-baseline">
            <span class="text-5xl font-extrabold tracking-tight text-base-fg-1"
              >{formatVND(sub.price)}</span
            >
            <span class="ml-1 text-xl font-semibold text-base-fg-3">/tháng</span
            >
          </div>
        </div>

        <ul class="space-y-4 mb-10 flex-1">
          <li class="flex items-start">
            <Check class="w-5 h-5 text-green-500 mr-3 mt-0.5" />
            <span class="text-base-fg-1 font-medium"
              ><b>{sub.aiLimit}</b> lượt phân tích AI / ngày</span
            >
          </li>
          <li class="flex items-start">
            <Check class="w-5 h-5 text-green-500 mr-3 mt-0.5" />
            <span class="text-base-fg-1">Tự động hóa Kanban Pipeline</span>
          </li>
          <li class="flex items-start">
            <Check class="w-5 h-5 text-green-500 mr-3 mt-0.5" />
            <span class="text-base-fg-1">Trích xuất CV không giới hạn</span>
          </li>
          {#if isPro}
            <li class="flex items-start">
              <Check class="w-5 h-5 text-green-500 mr-3 mt-0.5" />
              <span class="text-base-fg-1 font-semibold"
                >So sánh ứng viên song song</span
              >
            </li>
            <li class="flex items-start">
              <Check class="w-5 h-5 text-green-500 mr-3 mt-0.5" />
              <span class="text-base-fg-1">Hỗ trợ ưu tiên 24/7</span>
            </li>
          {/if}
        </ul>

        <div class="mt-auto">
          {#if isFree}
            <div></div>
          {:else}
            <button
              class="w-full py-4 px-6 rounded-xl font-bold transition-all
      {isPro
                ? 'bg-primary-1 text-base-1 hover:bg-primary-hover  cursor-pointer shadow-lg shadow-blue-200'
                : 'bg-primary-1 text-base-1 hover:bg-primary-hover'} cursor-pointer"
              onclick={() => handleUpgrade(sub.id)}
            >
              {isPro ? "Bắt đầu dùng thử PRO" : "Nâng cấp ngay"}
            </button>
          {/if}
        </div>
      </div>
    {/each}
  </div>
</div>
