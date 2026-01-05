export const renderHighlightedText = (
  originalText: string,
  highlights: any[]
) => {
  if (!originalText) return "";
  if (!highlights || highlights.length === 0) return originalText;

  let processedHtml = originalText;

  highlights.forEach((h) => {
    if (originalText.includes(h.text)) {
      const colorClass =
        h.type === "positive"
          ? "bg-green-200 text-green-900 px-1 rounded border border-green-300 cursor-help"
          : "bg-red-200 text-red-900 px-1 rounded border border-red-300 cursor-help";
      processedHtml = processedHtml.replaceAll(
        h.text,
        `<span class="${colorClass}" title="${h.comment}">${h.text}</span>`
      );
    }
  });
  return processedHtml;
};

export const formatLabel = (str: string) => {
  return str.replace(/-/g, " ").replace(/\b\w/g, (char) => char.toUpperCase());
};
