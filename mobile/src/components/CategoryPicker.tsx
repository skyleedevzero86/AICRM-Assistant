import { useEffect, useState } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { fetchConsultationCategoryTree } from "@/api/categories";
import { ApiError } from "@/api/client";
import type { ConsultationCategoryTreeNode } from "@/api/types";
import { ErrorMessage } from "@/components/ErrorMessage";
import { LoadingView } from "@/components/LoadingView";
import { isLeafCategory } from "@/utils/category-utils";

type CategoryPickerProps = {
  value: number | null;
  onChange: (categoryId: number | null) => void;
  error?: string;
};

export function CategoryPicker({ value, onChange, error }: CategoryPickerProps) {
  const [tree, setTree] = useState<ConsultationCategoryTreeNode[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [rootId, setRootId] = useState<number | null>(null);
  const [midId, setMidId] = useState<number | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError(null);
    fetchConsultationCategoryTree()
      .then((data) => {
        if (!active) return;
        setTree(data);
      })
      .catch((err) => {
        if (!active) return;
        setLoadError(err instanceof ApiError ? err.message : "상담 구분 목록을 불러오지 못했습니다.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    if (!value || tree.length === 0) return;
    for (const root of tree) {
      if (root.id === value) {
        setRootId(root.id);
        setMidId(null);
        return;
      }
      for (const mid of root.children) {
        if (mid.id === value) {
          setRootId(root.id);
          setMidId(mid.id);
          return;
        }
        for (const leaf of mid.children) {
          if (leaf.id === value) {
            setRootId(root.id);
            setMidId(mid.id);
            return;
          }
        }
      }
    }
  }, [tree, value]);

  const rootOptions = tree;
  const rootNode = rootOptions.find((node) => node.id === rootId) ?? null;
  const midOptions = rootNode?.children ?? [];
  const midNode = midOptions.find((node) => node.id === midId) ?? null;
  const leafOptions = midNode?.children ?? [];

  function selectRoot(id: number) {
    setRootId(id);
    setMidId(null);
    const node = rootOptions.find((item) => item.id === id);
    if (node && isLeafCategory(node)) {
      onChange(node.id);
      return;
    }
    onChange(null);
  }

  function selectMid(id: number) {
    setMidId(id);
    onChange(null);
    const node = midOptions.find((item) => item.id === id);
    if (node && isLeafCategory(node)) {
      onChange(node.id);
    }
  }

  function selectLeaf(id: number) {
    onChange(id);
  }

  if (loading) {
    return <LoadingView label="상담 구분을 불러오는 중..." />;
  }

  if (loadError) {
    return <ErrorMessage message={loadError} />;
  }

  return (
    <View style={styles.wrapper}>
      <Text style={styles.label}>문의 구분</Text>
      <LevelRow
        emptyLabel="대분류를 선택하세요"
        onSelect={selectRoot}
        options={rootOptions}
        selectedId={rootId}
      />
      {rootId ? (
        <LevelRow
          emptyLabel="중분류를 선택하세요"
          onSelect={selectMid}
          options={midOptions}
          selectedId={midId}
        />
      ) : null}
      {midId && leafOptions.length > 0 ? (
        <LevelRow
          emptyLabel="소분류를 선택하세요"
          onSelect={selectLeaf}
          options={leafOptions}
          selectedId={value}
        />
      ) : null}
      {error ? <Text style={styles.error}>{error}</Text> : null}
    </View>
  );
}

function LevelRow({
  options,
  selectedId,
  onSelect,
  emptyLabel
}: {
  options: ConsultationCategoryTreeNode[];
  selectedId: number | null;
  onSelect: (id: number) => void;
  emptyLabel: string;
}) {
  if (options.length === 0) {
    return <Text style={styles.hint}>{emptyLabel}</Text>;
  }

  return (
    <View style={styles.row}>
      {options.map((option) => {
        const active = selectedId === option.id;
        return (
          <TouchableOpacity
            key={option.id}
            onPress={() => onSelect(option.id)}
            style={[styles.chip, active && styles.chipActive]}
          >
            <Text style={[styles.chipText, active && styles.chipTextActive]}>{option.name}</Text>
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    gap: 8
  },
  label: {
    color: "#3f3f46",
    fontSize: 14,
    fontWeight: "800"
  },
  row: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8
  },
  chip: {
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 999,
    borderWidth: 1,
    paddingHorizontal: 12,
    paddingVertical: 8
  },
  chipActive: {
    backgroundColor: "#ecfdf5",
    borderColor: "#0f766e"
  },
  chipText: {
    color: "#3f3f46",
    fontSize: 13,
    fontWeight: "700"
  },
  chipTextActive: {
    color: "#0f766e"
  },
  hint: {
    color: "#71717a",
    fontSize: 13
  },
  error: {
    color: "#b91c1c",
    fontSize: 13
  }
});
