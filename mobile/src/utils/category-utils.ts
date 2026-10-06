import type { ConsultationCategoryTreeNode } from "@/api/types";

export function findCategoryNode(
  nodes: ConsultationCategoryTreeNode[],
  id: number
): ConsultationCategoryTreeNode | undefined {
  for (const node of nodes) {
    if (node.id === id) return node;
    const child = findCategoryNode(node.children, id);
    if (child) return child;
  }
  return undefined;
}

export function isLeafCategory(node: ConsultationCategoryTreeNode): boolean {
  return node.children.length === 0;
}
