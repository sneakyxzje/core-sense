type DateInput = string | number | Date | undefined | null;

export const formatDate = (
  date: DateInput,
  options: Intl.DateTimeFormatOptions = {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  },
  locale = "vi-VN"
) => {
  if (!date) return "-";

  const d = new Date(date);
  if (isNaN(d.getTime())) return "Ngày không hợp lệ";

  return new Intl.DateTimeFormat(locale, options).format(d);
};

export const formatDateTime = (date: DateInput) => {
  return formatDate(date, {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  });
};

export const formatRelativeTime = (date: DateInput, locale = "vi-VN") => {
  if (!date) return "-";

  const d = new Date(date);
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - d.getTime()) / 1000);

  const rtf = new Intl.RelativeTimeFormat(locale, { numeric: "auto" });

  if (diffInSeconds < 60) return "Vừa mới xong";
  if (diffInSeconds < 3600)
    return rtf.format(-Math.floor(diffInSeconds / 60), "minute");
  if (diffInSeconds < 86400)
    return rtf.format(-Math.floor(diffInSeconds / 3600), "hour");

  return formatDate(date);
};

export function parseDate(dateString: string): string {
  if (!dateString) return new Date().toISOString();

  let safeDate = dateString.replace(/(\.\d{3})\d+/, "$1");

  return safeDate;
}
