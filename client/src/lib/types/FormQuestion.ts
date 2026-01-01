export type FormQuestion = {
  id: string;
  type: "text" | "number" | "select" | "textarea" | "email" | "files";
  label: string;
  placeholder?: string;
  required: boolean;
  options?: string[];
  isSystem: boolean;
};
