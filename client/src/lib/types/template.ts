export interface Template {
  slug: string;
  name: string;
  subject: string;
  description: string;
  customBody: string;
  defaultBody: string;
}

export interface MarketTemplate {
  id: string;
  displayName: string;
  description: string;
  category: string;
  content: string;
  defaultBody: string;
}
