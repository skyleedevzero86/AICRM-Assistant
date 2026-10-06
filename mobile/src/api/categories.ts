import { apiRequest } from "./client";
import type { ConsultationCategoryTreeNode } from "./types";

function normalizeTree(nodes: ConsultationCategoryTreeNode[] | null | undefined): ConsultationCategoryTreeNode[] {
  if (!nodes?.length) return [];

  return nodes.map((node) => ({
    id: node.id,
    code: node.code,
    name: node.name,
    depth: node.depth,
    children: normalizeTree(node.children)
  }));
}

export function fetchConsultationCategoryTree(): Promise<ConsultationCategoryTreeNode[]> {
  return apiRequest<ConsultationCategoryTreeNode[]>("/api/categories/consultation/tree").then(normalizeTree);
}
