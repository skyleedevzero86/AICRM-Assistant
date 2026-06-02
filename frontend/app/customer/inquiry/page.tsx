"use client";

import { useMutation, useQuery } from "@tanstack/react-query";
import type { Route } from "next";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { FormField } from "@/components/form-field";
import { PageShell } from "@/components/page-shell";
import { fetchConsultationCategoryTree } from "@/lib/api/categories";
import { createCustomerInquiry } from "@/lib/api/customer";
import { ApiError } from "@/lib/api/client";
import { collectLeafCategoryOptions } from "@/lib/category-utils";
import { formatTicketStatus } from "@/lib/format";
import {
  loadRecentInquiries,
  saveRecentInquiry,
  type RecentInquiryRecord
} from "@/lib/recent-inquiries";

const inputClassName =
  "w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600";

export default function CustomerInquiryPage() {
  const router = useRouter();
  const [customerName, setCustomerName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [recentInquiries, setRecentInquiries] = useState<RecentInquiryRecord[]>([]);

  useEffect(() => {
    setRecentInquiries(loadRecentInquiries());
  }, []);

  const categoryQuery = useQuery({
    queryKey: ["consultation-category-tree"],
    queryFn: fetchConsultationCategoryTree
  });

  const categoryOptions = collectLeafCategoryOptions(categoryQuery.data ?? []);

  const submitMutation = useMutation({
    mutationFn: createCustomerInquiry,
    onSuccess: (data, variables) => {
      setFieldErrors({});
      const record = saveRecentInquiry({
        ...data,
        title: variables.title,
        customerName: variables.customerName,
        submittedAt: new Date().toISOString()
      });
      setRecentInquiries(record);
    }
  });

  function resetForm() {
    setCustomerName("");
    setPhone("");
    setEmail("");
    setCategoryId("");
    setTitle("");
    setContent("");
    setFieldErrors({});
    submitMutation.reset();
  }

  function validateForm(): boolean {
    const errors: Record<string, string> = {};
    if (!customerName.trim()) errors.customerName = "이름을 입력해 주세요.";
    if (!phone.trim()) errors.phone = "연락처를 입력해 주세요.";
    if (!email.trim()) errors.email = "이메일을 입력해 주세요.";
    if (!categoryId) errors.categoryId = "문의 구분을 선택해 주세요.";
    if (!title.trim()) errors.title = "제목을 입력해 주세요.";
    if (!content.trim()) errors.content = "문의 내용을 입력해 주세요.";
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!validateForm()) return;

    submitMutation.mutate({
      customerName: customerName.trim(),
      phone: phone.trim(),
      email: email.trim(),
      categoryId: Number(categoryId),
      title: title.trim(),
      content: content.trim()
    });
  }

  const errorMessage =
    submitMutation.error instanceof ApiError
      ? submitMutation.error.message
      : submitMutation.error
        ? "문의 접수에 실패했습니다."
        : null;

  return (
    <PageShell
      title="고객 문의"
      description="문의 내용을 작성하시면 상담원이 확인 후 답변드립니다."
    >
      <div className="space-y-4">
        {submitMutation.isSuccess && submitMutation.data ? (
          <AlertBanner
            message={`문의가 접수되었습니다. 티켓 번호: ${submitMutation.data.ticketNo} (상태: ${formatTicketStatus(submitMutation.data.status)})`}
            variant="success"
          />
        ) : null}
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        {categoryQuery.isError ? (
          <AlertBanner message="상담 구분 목록을 불러오지 못했습니다." variant="error" />
        ) : null}

        {recentInquiries.length > 0 ? (
          <section className="rounded-lg border border-zinc-200 bg-white p-4">
            <h2 className="mb-3 text-sm font-semibold text-zinc-800">이번 세션 접수 내역</h2>
            <ul className="divide-y divide-zinc-100 text-sm">
              {recentInquiries.map((inquiry) => (
                <li className="flex flex-wrap items-baseline justify-between gap-2 py-2" key={inquiry.ticketId}>
                  <div>
                    <span className="font-medium text-zinc-900">{inquiry.ticketNo}</span>
                    <span className="mx-2 text-zinc-300">·</span>
                    <span className="text-zinc-700">{inquiry.title}</span>
                    <span className="ml-2 text-xs text-zinc-500">({inquiry.customerName})</span>
                  </div>
                  <span className="text-xs text-zinc-500">{formatTicketStatus(inquiry.status)}</span>
                </li>
              ))}
            </ul>
            <p className="mt-2 text-xs text-zinc-500">브라우저 탭을 닫으면 목록이 사라집니다.</p>
          </section>
        ) : null}

        <form
          className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6"
          onSubmit={handleSubmit}
        >
          <FormField error={fieldErrors.customerName} htmlFor="customerName" label="이름" required>
            <input
              className={inputClassName}
              id="customerName"
              onChange={(event) => setCustomerName(event.target.value)}
              type="text"
              value={customerName}
            />
          </FormField>

          <FormField error={fieldErrors.phone} htmlFor="phone" label="연락처" required>
            <input
              className={inputClassName}
              id="phone"
              onChange={(event) => setPhone(event.target.value)}
              type="tel"
              value={phone}
            />
          </FormField>

          <FormField error={fieldErrors.email} htmlFor="email" label="이메일" required>
            <input
              className={inputClassName}
              id="email"
              onChange={(event) => setEmail(event.target.value)}
              type="email"
              value={email}
            />
          </FormField>

          <FormField error={fieldErrors.categoryId} htmlFor="categoryId" label="문의 구분" required>
            <select
              className={inputClassName}
              disabled={categoryQuery.isLoading || categoryOptions.length === 0}
              id="categoryId"
              onChange={(event) => setCategoryId(event.target.value)}
              value={categoryId}
            >
              <option value="">
                {categoryQuery.isLoading ? "불러오는 중..." : "문의 구분을 선택하세요"}
              </option>
              {categoryOptions.map((option) => (
                <option key={option.id} value={option.id}>
                  {option.label}
                </option>
              ))}
            </select>
          </FormField>

          <FormField error={fieldErrors.title} htmlFor="title" label="제목" required>
            <input
              className={inputClassName}
              id="title"
              onChange={(event) => setTitle(event.target.value)}
              type="text"
              value={title}
            />
          </FormField>

          <FormField error={fieldErrors.content} htmlFor="content" label="문의 내용" required>
            <textarea
              className={`${inputClassName} min-h-[160px] resize-y`}
              id="content"
              onChange={(event) => setContent(event.target.value)}
              value={content}
            />
          </FormField>

          <div className="flex flex-wrap gap-2">
            <button
              className="rounded-md bg-teal-700 px-4 py-2 text-sm font-medium text-white hover:bg-teal-800 disabled:opacity-50"
              disabled={submitMutation.isPending}
              type="submit"
            >
              {submitMutation.isPending ? "접수 중..." : "접수하기"}
            </button>
            <button
              className="rounded-md border border-zinc-300 bg-white px-4 py-2 text-sm font-medium text-zinc-700 hover:bg-zinc-50"
              onClick={resetForm}
              type="button"
            >
              입력 초기화
            </button>
          </div>
        </form>
      </div>
    </PageShell>
  );
}
