<script lang="ts">
  import { page } from "$app/state";
  import * as Breadcrumb from "$lib/components/ui/breadcrumb/index.js";
  import {
    Bell,
    CircleAlert,
    MessageSquare,
    MessageSquareQuote,
    MoonIcon,
    SunIcon,
  } from "lucide-svelte";
  import { toggleMode } from "mode-watcher";
  import * as DropdownMenu from "$lib/components/ui/dropdown-menu";
  import { ScrollArea } from "$lib/components/ui/scroll-area";
  import * as Sidebar from "$lib/components/ui/sidebar/index.js";
  import PanelLeft from "@lucide/svelte/icons/panel-left";
  import { notificationStore } from "@src/lib/stores/notification.svelte";
  import { formatRelativeTime } from "@src/lib/utils/dateUtils";
  import { formatLabel } from "@src/lib/utils/textUtils";
  const sidebar = Sidebar.useSidebar();
  function getBreadcrumbLabel(segment: string) {
    const data = page.data;

    if (data.campaignId && data.campaignName) {
      if (segment === data.campaignId) {
        return data.campaignName;
      }
    }
    return formatLabel(segment);
  }
</script>

<div class=" flex justify-between items-center px-4 py-2">
  <div class="flex items-center gap-4">
    <button
      onclick={sidebar.toggle}
      class="p-2 hover:bg-accent rounded-md transition-colors"
      title="Toggle Sidebar"
    >
      <PanelLeft
        class="h-5 w-5 transition-transform {sidebar.open ? '' : 'rotate-180'}"
      />
    </button>
    <Breadcrumb.Root>
      <Breadcrumb.List>
        {@const paths = page.url.pathname
          .split("/")
          .filter(
            (t) =>
              t !== "" &&
              t !== page.params.submissionId &&
              t.toLowerCase() !== "dashboard"
          )}
        {#if paths.length === 0}
          <Breadcrumb.Item>
            <Breadcrumb.Page class="font-bold">Dashboard</Breadcrumb.Page>
          </Breadcrumb.Item>
        {:else}
          <Breadcrumb.Item>
            <Breadcrumb.Link href="/dashboard">Dashboard</Breadcrumb.Link>
          </Breadcrumb.Item>
        {/if}

        {#each paths as segment, index}
          {@const href = "/" + paths.slice(0, index + 1).join("/")}

          {@const label = getBreadcrumbLabel(segment)}

          {@const isLast = index === paths.length - 1}

          <Breadcrumb.Separator />

          <Breadcrumb.Item>
            {#if isLast}
              <Breadcrumb.Page class="font-bold">{label}</Breadcrumb.Page>
            {:else}
              <Breadcrumb.Link {href}>
                {label}
              </Breadcrumb.Link>
            {/if}
          </Breadcrumb.Item>
        {/each}
      </Breadcrumb.List>
    </Breadcrumb.Root>
  </div>
  <div>
    <div class="flex items-center gap-2">
      <DropdownMenu.Root>
        <DropdownMenu.Trigger
          class="relative p-2 hover:bg-accent rounded-full transition-colors"
        >
          <Bell class="h-[1.2rem] w-[1.2rem] cursor-pointer" />

          {#if notificationStore.unreadCount > 0}
            <span
              class="absolute top-1 right-1 flex h-4 w-4 items-center justify-center rounded-full bg-negative-1 text-[10px] text-base-1 font-bold border-2 border-base-border-3"
            >
              {notificationStore.unreadCount}
            </span>
          {/if}
        </DropdownMenu.Trigger>

        <DropdownMenu.Content
          class="w-80 p-0 border-base-border-1 border bg-base-2"
          align="end"
        >
          <div
            class="px-4 py-2 border-b border-base-border-1 flex justify-between items-center"
          >
            <span class="font-bold text-sm">Thông báo</span>
            <button
              onclick={() => notificationStore.markAllAsRead()}
              class="text-[10px] text-muted-foreground cursor-pointer hover:underline transition-colors"
            >
              Đánh dấu đã đọc
            </button>
          </div>

          <ScrollArea class="h-[300px]">
            {#if notificationStore.list.length === 0}
              <div class="p-4 text-center text-xs text-muted-foreground">
                Không có thông báo mới
              </div>
            {:else}
              {#each notificationStore.list as noti}
                <DropdownMenu.Item
                  class="flex flex-col hover:bg-base-3 items-start gap-1 p-4 cursor-pointer border-b border-base-border-1 last:border-0 {noti.isRead
                    ? 'opacity-60'
                    : 'bg-base-3'}"
                  onclick={(e) => {
                    e.stopPropagation();
                    notificationStore.markAsRead(noti.id);
                  }}
                >
                  <div
                    class="font-semibold text-xs uppercase text-negative-1 flex items-center gap-2 {noti.type ===
                    'INTERVIEW'
                      ? 'text-negative-1'
                      : 'text-blue-500'}"
                  >
                    <div>
                      {#if noti.type === "INTERVIEW"}
                        <CircleAlert class="size-3" />
                      {:else}
                        <MessageSquareQuote class="size-3" />
                      {/if}
                    </div>
                    <div>
                      {noti.title}
                    </div>
                  </div>
                  <div class="text-[11px] text-muted-foreground line-clamp-2">
                    {noti.message}
                  </div>
                  <div class="text-[9px] mt-1 opacity-50">
                    {formatRelativeTime(noti.createdAt)}
                  </div>
                </DropdownMenu.Item>
              {/each}
            {/if}
          </ScrollArea>
        </DropdownMenu.Content>
      </DropdownMenu.Root>
      <button
        onclick={toggleMode}
        type="button"
        class="
      relative inline-flex h-9 w-9 items-center justify-center rounded-md
      text-foreground transition-colors
      hover:bg-accent/50
      focus-visible:outline-none focus-visible:ring-0
      cursor-pointer
    "
      >
        <SunIcon
          class="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all duration-300 dark:-rotate-90 dark:scale-0"
        />

        <MoonIcon
          class="absolute h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all duration-300 dark:rotate-0 dark:scale-100"
        />
      </button>
    </div>
  </div>
</div>
