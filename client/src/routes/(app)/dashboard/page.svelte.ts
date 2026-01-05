import {
  createSocketClient,
  subscribeSubmissions,
} from "@src/lib/services/Socket";
import type {
  SubmissionChart,
  SubmissionEvent,
  SubmissionSummary,
  TotalSubmissions,
} from "@src/lib/types/submission";
import { parseDate } from "@src/lib/utils/FormatDate";
import type { Client } from "@stomp/stompjs";

export class DashboardState {
  stats = $state<TotalSubmissions>();
  chart = $state<SubmissionChart[]>([]);
  summary = $state<SubmissionSummary[]>([]);
  stompClient: Client | null = null;
  private unsubscribe: (() => void) | null = null;
  constructor(initialData: {
    stats: TotalSubmissions;
    chart: SubmissionChart[];
    summary: SubmissionSummary[];
  }) {
    (this.stats = initialData.stats),
      (this.chart = initialData.chart ?? []),
      (this.summary = initialData.summary ?? []);
  }

  statsCards = $derived([
    {
      id: "active",
      title: "Chiến dịch đang chạy",
      value: this.stats?.activeCampaigns ?? 0,
    },
    {
      id: "total",
      title: "Tổng lượt nộp",
      value: this.stats?.totalSubmissions ?? 0,
    },
    {
      id: "ratio",
      title: "Tỷ lệ phản hồi chất lượng cao ( >= 8 điểm )",
      value: this.stats ? `${this.stats.highQualityRatio.toFixed(2)}%` : "0%",
    },
  ]);

  chartItems = $derived.by(() => {
    const raw = this.chart ?? [];
    const max = Math.max(...raw.map((d) => d.value), 1);
    return raw.map((item) => ({
      label: item.timePoint.split("/")[0] + "/" + item.timePoint.split("/")[1],
      fullDate: item.timePoint,
      count: item.value,
      heightPercentage:
        (item.value / max) * 100 > 0 ? (item.value / max) * 100 : 2,
    }));
  });

  connectSocket() {
    const client = createSocketClient();
    if (!client.activate) client.activate();
    this.unsubscribe = subscribeSubmissions(this.onMessageReceived.bind(this));
  }

  disconnectSocket() {
    if (this.unsubscribe) this.unsubscribe;
  }
  private onMessageReceived(payload: SubmissionEvent) {
    if (this.stats) {
      this.stats.totalSubmissions += 1;
    }
    this.updateChart(payload.submittedAt);

    this.updateSummary(payload.campaignName, payload.submittedAt);
  }
  private updateSummary(campaignName: string, submittedAt: string) {
    const safeDate = parseDate(submittedAt);
    const newItem = { campaignName, submittedAt: safeDate };
    this.summary = [newItem, ...this.summary].slice(0, 5);
  }

  private updateChart(dateString: string) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    const timePointKey = `${day}/${month}/${year}`;

    const existingIndex = this.chart.findIndex(
      (c) => c.timePoint === timePointKey
    );

    const newChart = [...this.chart];
    if (existingIndex !== -1) {
      newChart[existingIndex].value += 1;
    } else {
      newChart.push({ timePoint: timePointKey, value: 1 });
    }
    this.chart = newChart;
  }
}
