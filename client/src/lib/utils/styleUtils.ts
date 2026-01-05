export const getScoreColor = (score: number) => {
  if (!score) return "text-slate-500 bg-slate-100 border-slate-200";
  if (score >= 8) return "text-primary-fg-1 bg-positive-5";
  if (score >= 5) return "text-primary-fg-1 bg-priority-medium";
  return "text-primary-fg-1 bg-negative-1 ";
};
