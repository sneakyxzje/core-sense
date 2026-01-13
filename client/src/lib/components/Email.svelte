<script lang="ts">
  import { onMount, onDestroy } from "svelte";
  import { Editor } from "@tiptap/core";
  import StarterKit from "@tiptap/starter-kit";
  import Underline from "@tiptap/extension-underline";
  import { Placeholder } from "@tiptap/extensions";
  import {
    Bold,
    Italic,
    Underline as UnderlineIcon,
    List,
  } from "lucide-svelte";

  let { content = $bindable(), placeholder = "Bắt đầu viết thư..." } = $props();
  let element = $state<HTMLElement>();
  let editor = $state<Editor | null>(null);
  $effect(() => {
    if (editor && content !== editor.getHTML()) {
      editor.commands.setContent(content);
    }
  });
  onMount(() => {
    editor = new Editor({
      element: element,
      extensions: [
        StarterKit,
        Underline,
        Placeholder.configure({ placeholder }),
      ],
      content: content,
      onUpdate: ({ editor }) => {
        content = editor.getHTML();
      },
      editorProps: {
        attributes: {
          class:
            "outline-none min-h-[200px] p-4 prose prose-sm max-w-none  text-sm leading-relaxed",
        },
      },
    });
  });

  onDestroy(() => {
    if (editor) editor.destroy();
  });
</script>

<div
  class="flex flex-col w-full bg-white border border-gray-200 rounded-lg overflow-hidden shadow-sm"
>
  <div class="flex items-center gap-1 p-1 bg-gray-50 border-b border-gray-100">
    {#if editor}
      <button
        onclick={() => editor?.chain().focus().toggleBold().run()}
        class="p-1.5 rounded hover:bg-gray-200 {editor.isActive('bold')
          ? 'text-blue-600 bg-blue-50'
          : 'text-gray-500'}"
      >
        <Bold size={14} />
      </button>
      <button
        onclick={() => editor?.chain().focus().toggleItalic().run()}
        class="p-1.5 rounded hover:bg-gray-200 {editor.isActive('italic')
          ? 'text-blue-600 bg-blue-50'
          : 'text-gray-500'}"
      >
        <Italic size={14} />
      </button>
      <button
        onclick={() => editor?.chain().focus().toggleUnderline().run()}
        class="p-1.5 rounded hover:bg-gray-200 {editor.isActive('underline')
          ? 'text-blue-600 bg-blue-50'
          : 'text-gray-500'}"
      >
        <UnderlineIcon size={14} />
      </button>
      <button
        onclick={() => editor?.chain().focus().toggleBulletList().run()}
        class="p-1.5 rounded hover:bg-gray-200 {editor.isActive('bulletList')
          ? 'text-blue-600 bg-blue-50'
          : 'text-gray-500'}"
      >
        <List size={14} />
      </button>
    {/if}
  </div>

  <div bind:this={element} class="flex-1 overflow-y-auto max-h-60"></div>
</div>

<style>
  :global(.tiptap p.is-editor-empty:first-child::before) {
    color: #adb5bd;
    content: attr(data-placeholder);
    float: left;
    height: 0;
    pointer-events: none;
  }
</style>
