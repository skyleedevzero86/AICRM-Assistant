import { describe, expect, it } from "vitest";
import type { ConsultationCategoryTreeNode } from "./api/types";
import { collectLeafCategoryOptions } from "./category-utils";

describe("collectLeafCategoryOptions", () => {
  it("collects only leaf categories with full path label", () => {
    const tree: ConsultationCategoryTreeNode[] = [
      {
        id: 1,
        code: "ROOT",
        name: "일반 문의",
        depth: 1,
        children: [
          {
            id: 2,
            code: "LOGIN",
            name: "로그인",
            depth: 2,
            children: []
          }
        ]
      },
      {
        id: 3,
        code: "BILLING",
        name: "결제",
        depth: 1,
        children: []
      }
    ];

    const options = collectLeafCategoryOptions(tree);

    expect(options).toEqual([
      { id: 2, label: "일반 문의 > 로그인" },
      { id: 3, label: "결제" }
    ]);
  });
});
