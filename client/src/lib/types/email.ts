export interface EmailInteface {
  submissionId: string;
  to: string;
  subject: string;
  body: string;
  slug: string;
  variables?: Object;
}
