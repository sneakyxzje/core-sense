<script lang="ts">
  import { page } from "$app/state";
  import * as Breadcrumb from "$lib/components/ui/breadcrumb/index.js";
  import { MoonIcon, SunIcon } from "lucide-svelte";
  import { toggleMode } from "mode-watcher";
  import * as Sidebar from "$lib/components/ui/sidebar/index.js";
  import PanelLeft from "@lucide/svelte/icons/panel-left";
  function formatLabel(str: string) {
    return str
      .replace(/-/g, " ")
      .replace(/\b\w/g, (char) => char.toUpperCase());
  }
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
