<script lang="ts">
  import { ChevronDown } from "lucide-svelte";
  let { data } = $props();
  let interviews = $derived(data.allInterview);
  let upcomingInterview = $derived(data.upCommingInterview);
  import * as Accordion from "$lib/components/ui/accordion";
  import Button from "@src/lib/components/ui/button/button.svelte";
  import { formatDateLocal } from "@src/lib/utils/dateUtils.js";

  const testDay = $derived.by(() => {
    const days = [];
    const today = new Date();
    for (let i = 0; i < 7; i++) {
      const date = new Date();
      date.setDate(today.getDate() + i);
      let label = "";
      if (i === 0) {
        label = "Hôm nay";
      } else {
        label = date.toLocaleDateString("vi-VN", { weekday: "long" });
      }
      const dateKey = formatDateLocal(date);
      const displayDate = `${date.getDate()}/${date.getMonth() + 1}`;

      days.push({
        label: `${label} (${displayDate})`,
        dateKey: dateKey,
        value: date.getDay(),
      });
    }
    return days;
  });

  let viewDate = $state(new Date());

  const calendarDays = $derived.by(() => {
    const year = viewDate.getFullYear();
    const month = viewDate.getMonth();
    const days = [];

    const totalDays = new Date(year, month + 1, 0).getDate();
    const firstDay = new Date(year, month, 1).getDay();

    const padding = firstDay === 0 ? 6 : firstDay - 1;
    const totalCells = padding + totalDays;
    for (let i = padding - 1; i >= 0; i--) {
      days.push(new Date(year, month, -i));
    }

    for (let d = 1; d <= totalDays; d++) {
      days.push(new Date(year, month, d));
    }
    const remainingSlots = 42 - days.length;
    for (let i = 1; i <= remainingSlots; i++) {
      days.push(new Date(year, month, totalDays + i));
    }
    return days;
  });

  function getInterviewsForDay(dayValue: number) {
    return upcomingInterview
      .filter((i) => {
        return (
          formatDateLocal(new Date(i.scheduleAt)) ===
          testDay?.find((d) => d.value === dayValue)?.dateKey
        );
      })
      .sort(
        (a, b) =>
          new Date(a.scheduleAt).getTime() - new Date(b.scheduleAt).getTime(),
      );
  }

  const isToday = (date: Date) => {
    const today = new Date();
    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    );
  };
  function getInterviewsForCalendarDay(date: Date) {
    const dateKey = formatDateLocal(date);
    return interviews.filter((i) => {
      return formatDateLocal(new Date(i.scheduleAt)) === dateKey;
    });
  }
</script>

<div class="space-y-6">
  <div class="flex items-center justify-between">
    <div>
      <h2 class="text-3xl font-bold tracking-tight">Phỏng vấn</h2>
      <p class="text-base-fg-4 mt-1">
        Theo dõi và lên kế hoạch làm việc của bạn
      </p>
    </div>
  </div>
</div>

<div class="mt-8 overflow-x-auto pb-4">
  <h2 class="text-xl font-bold mb-6 text-base-fg-1 flex items-center gap-2">
    <span class="w-2 h-6 bg-primary-1 rounded-full"></span>
    Lịch phỏng vấn trong 7 ngày tới
  </h2>

  <div class="grid grid-cols-1 md:grid-cols-7 gap-3 min-w-[1000px] h-[300px]">
    {#each testDay as day}
      <div
        class="flex flex-col border border-base-border-1 rounded-xl bg-base-4 shadow-sm overflow-hidden"
      >
        <div
          class="p-3 border-b border-base-border-1 bg-base-3 flex items-center justify-center gap-3"
        >
          <div class="h-[1px] flex-grow bg-base-border-1"></div>

          <div class="flex items-center gap-2 shrink-0">
            <span
              class="text-sm font-bold text-base-fg-2 tracking-wide uppercase"
            >
              {day.label}
            </span>
          </div>

          <div class="h-[1px] flex-grow bg-base-border-1"></div>
        </div>

        <div class="p-2 flex-grow overflow-y-auto scrollbar-thin">
          <Accordion.Root type="single" class="w-full">
            {#each getInterviewsForDay(day.value) as i (i.id)}
              <Accordion.Item value={i.id.toString()} class="border-none mb-2">
                <Accordion.Trigger
                  class="p-0 hover:no-underline [&>svg]:hidden group w-full"
                >
                  <div
                    class="flex w-full items-stretch justify-between text-sm bg-base-3 border border-base-border-2 rounded-lg shadow-sm group-data-[state=open]:rounded-b-none group-data-[state=open]:border-b-0 hover:border-base-border-hover transition-all duration-200"
                  >
                    <div
                      class="p-3 border-r border-base-border-1 shrink-0 flex items-center"
                    >
                      <span class="font-bold text-[11px] text-base-fg-1">
                        {new Date(i.scheduleAt).toLocaleTimeString("vi-VN", {
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </span>
                    </div>

                    <div
                      class="px-3 flex-grow text-left overflow-hidden border-r border-base-border-1 flex items-center"
                    >
                      <p class="font-medium text-base-fg-1 line-clamp-1">
                        {i.candidateName}
                      </p>
                    </div>

                    <div
                      class="px-3 flex items-center justify-center shrink-0 transition-transform duration-300 group-data-[state=open]:rotate-180"
                    >
                      <ChevronDown size={18} class="text-base-fg-2" />
                    </div>
                  </div>
                </Accordion.Trigger>
                <Accordion.Content
                  class="bg-base-3 border-x border-b border-base-border-2 rounded-b-lg px-3 pb-3"
                >
                  <div
                    class="pt-3 border-t border-base-border-1 text-sm text-base-fg-2 max-h-[100px] overflow-y-auto scrollbar-thin"
                  >
                    <p class="mb-2 text-[9px] text-base-fg-4">
                      {i.campaignName}
                    </p>
                    {#if i.notes}
                      <p class="mt-1 opacity-80 whitespace-pre-wrap">
                        📝 {i.notes}
                      </p>
                    {/if}
                  </div>
                </Accordion.Content>
              </Accordion.Item>
            {:else}
              <div
                class="h-full flex flex-col items-center justify-center opacity-30 py-10"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-8 w-8 mb-1"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                  />
                </svg>
                <span class="text-xs italic">Trống</span>
              </div>
            {/each}
          </Accordion.Root>
        </div>
      </div>
    {/each}
  </div>
</div>
<div class="mt-12 space-y-6">
  <h2 class="text-xl font-bold mb-6 text-base-fg-1 flex items-center gap-2">
    <span class="w-2 h-6 bg-primary-1 rounded-full"></span>
    Lịch phỏng vấn trong tháng
  </h2>
  <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
    <div class="flex items-center gap-4">
      <h2 class="text-2xl font-bold text-base-fg-1 capitalize">
        {viewDate.toLocaleDateString("vi-VN", {
          month: "long",
          year: "numeric",
        })}
      </h2>
      <div class="flex bg-base-3 p-1 rounded-xl border border-base-border-2">
        <Button
          onclick={() =>
            (viewDate = new Date(
              viewDate.getFullYear(),
              viewDate.getMonth() - 1,
              1,
            ))}
          class="p-1.5 hover:bg-base-1 hover:shadow-sm rounded-lg transition-all text-base-fg-3"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"><path d="m15 18-6-6 6-6" /></svg
          >
        </Button>
        <button
          onclick={() => (viewDate = new Date())}
          class="px-3 text-xs font-bold text-base-fg-2 hover:text-primary-1 transition-colors"
        >
          Hôm nay
        </button>
        <Button
          onclick={() =>
            (viewDate = new Date(
              viewDate.getFullYear(),
              viewDate.getMonth() + 1,
              1,
            ))}
          class="p-1.5 hover:bg-base-1 hover:shadow-sm rounded-lg transition-all text-base-fg-3"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"><path d="m9 18 6-6-6-6" /></svg
          >
        </Button>
      </div>
    </div>
  </div>
  <div
    class="bg-base-1 border border-base-border-1 rounded-md overflow-hidden shadow-sm"
  >
    <div class="grid grid-cols-7 border-b border-base-border-2 bg-base-4">
      {#each ["Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"] as day}
        <div
          class="py-4 text-center text-[11px] font-bold text-base-fg-4 uppercase tracking-widest"
        >
          {day}
        </div>
      {/each}
    </div>
    <div class="grid grid-cols-7">
      {#each calendarDays as date}
        <div
          class="
    min-h-[120px] p-2 border-r border-b border-base-border-2
    {date.getMonth() !== viewDate.getMonth()
            ? 'pointer-events-none bg-base-4'
            : 'bg-base-3'}
    transition-all duration-200 hover:z-10 hover:shadow-lg hover:border-primary-1/30
"
        >
          <div class="flex justify-between items-center mb-1">
            <span
              class="text-xs font-bold {isToday(date)
                ? 'bg-primary-1 text-white px-2 py-1 rounded-full'
                : 'text-base-fg-4'}"
            >
              {date.getDate()}
            </span>

            {#if getInterviewsForCalendarDay(date).length > 0}
              <div
                class="w-1.5 h-1.5 rounded-full bg-primary-1 animate-pulse"
              ></div>
            {/if}
          </div>
          <div class="space-y-1">
            {#each getInterviewsForCalendarDay(date).slice(0, 3) as itv}
              <div
                class="px-1.5 py-0.5 text-[9px] bg-base-2 border border-base-border-3 rounded truncate font-medium text-base-fg-2"
              >
                {itv.candidateName}
              </div>
            {/each}
            {#if getInterviewsForCalendarDay(date).length > 3}
              <div class="text-[8px] text-base-fg-4 pl-1">
                + {getInterviewsForCalendarDay(date).length - 3} lịch nữa
              </div>
            {/if}
          </div>
        </div>
      {/each}
    </div>
  </div>
</div>

<style>
  .scrollbar-thin::-webkit-scrollbar {
    width: 4px;
  }
  .scrollbar-thin::-webkit-scrollbar-track {
    background: transparent;
  }
  .scrollbar-thin::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 10px;
  }
  .scrollbar-thin::-webkit-scrollbar-thumb:hover {
    background: #cbd5e1;
  }
</style>
