export interface EmailInteface {
  submissionId: string;
  to: string;
  subject: string;
  body: string;
  defaultBody: string;
  slug: string;
  variables?: Object;
}
