import type { ConsultationCategoryTreeNode } from "./api/types";

export type CategoryOption = {
  id: number;
  label: string;
};

export function collectLeafCategoryOptions(
  nodes: ConsultationCategoryTreeNode[],
  parentPath: string[] = []
): CategoryOption[] {
  const options: CategoryOption[] = [];

  for (const node of nodes) {
    const path = [...parentPath, node.name];
    if (node.children.length === 0) {
      options.push({ id: node.id, label: path.join(" > ") });
      continue;
    }
    options.push(...collectLeafCategoryOptions(node.children, path));
  }

  return options;
}
