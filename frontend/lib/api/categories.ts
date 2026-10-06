import { apiRequest } from "./client";
import type { ConsultationCategoryTreeNode } from "./types";

export function fetchConsultationCategoryTree(): Promise<ConsultationCategoryTreeNode[]> {
  return apiRequest<ConsultationCategoryTreeNode[]>("/api/categories/consultation/tree");
}
