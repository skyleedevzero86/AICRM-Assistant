"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRef, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import {
  fetchAttachmentDownloadUrl,
  fetchTicketAttachments,
  uploadAgentTicketAttachment,
  uploadCustomerTicketAttachment,
  type Attachment
} from "@/lib/api/attachments";
import { ApiError } from "@/lib/api/client";
import { msg } from "@/lib/messages";

type TicketAttachmentPanelProps = {
  ticketId: number;
  role: "CUSTOMER" | "AGENT";
  canUpload: boolean;
  messageId?: number;
  uploadErrorMessage: string;
  downloadErrorMessage: string;
  loadErrorMessage: string;
};

function formatFileSize(size: number): string {
  if (size < 1024) {
    return `${size}B`;
  }
  if (size < 1024 * 1024) {
    return `${Math.round(size / 1024)}KB`;
  }
  return `${(size / (1024 * 1024)).toFixed(1)}MB`;
}

export function TicketAttachmentPanel({
  ticketId,
  role,
  canUpload,
  messageId,
  uploadErrorMessage,
  downloadErrorMessage,
  loadErrorMessage
}: TicketAttachmentPanelProps) {
  const queryClient = useQueryClient();
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [downloadingId, setDownloadingId] = useState<number | null>(null);

  const attachmentsQuery = useQuery({
    queryKey: ["ticket-attachments", ticketId],
    queryFn: () => fetchTicketAttachments(ticketId),
    enabled: Number.isFinite(ticketId)
  });

  const uploadMutation = useMutation({
    mutationFn: (file: File) =>
      role === "CUSTOMER"
        ? uploadCustomerTicketAttachment(ticketId, file, messageId)
        : uploadAgentTicketAttachment(ticketId, file, messageId),
    onSuccess: () => {
      setActionError(null);
      queryClient.invalidateQueries({ queryKey: ["ticket-attachments", ticketId] });
      if (fileInputRef.current) {
        fileInputRef.current.value = "";
      }
    },
    onError: (error) => {
      setActionError(error instanceof ApiError ? error.message : uploadErrorMessage);
    }
  });

  async function handleDownload(attachment: Attachment) {
    setActionError(null);
    setDownloadingId(attachment.attachmentId);
    try {
      const result = await fetchAttachmentDownloadUrl(attachment.attachmentId);
      window.open(result.downloadUrl, "_blank", "noopener,noreferrer");
    } catch (error) {
      setActionError(error instanceof ApiError ? error.message : downloadErrorMessage);
    } finally {
      setDownloadingId(null);
    }
  }

  function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }
    setActionError(null);
    uploadMutation.mutate(file);
  }

  return (
    <section className="rounded-lg border border-zinc-200 bg-white p-5">
      <div className="mb-3 flex flex-wrap items-center justify-between gap-2">
        <h2 className="text-base font-semibold">{msg.ui("common.attachment")}</h2>
        {canUpload ? (
          <>
            <input
              ref={fileInputRef}
              accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.xls,.xlsx,.txt,.zip"
              className="hidden"
              onChange={handleFileChange}
              type="file"
            />
            <button
              className="rounded-md border border-zinc-300 bg-white px-3 py-1.5 text-sm font-medium text-zinc-700 hover:bg-zinc-50 disabled:opacity-50"
              disabled={uploadMutation.isPending}
              onClick={() => fileInputRef.current?.click()}
              type="button"
            >
              {uploadMutation.isPending ? msg.ui("common.uploadingAttachment") : msg.ui("common.uploadAttachment")}
            </button>
          </>
        ) : null}
      </div>

      {actionError ? <AlertBanner message={actionError} variant="error" /> : null}
      {attachmentsQuery.isError ? <AlertBanner message={loadErrorMessage} variant="error" /> : null}
      {attachmentsQuery.isLoading ? <p className="text-sm text-zinc-500">{msg.ui("common.loading")}</p> : null}

      <ul className="space-y-2">
        {attachmentsQuery.data?.map((attachment) => (
          <li className="flex flex-wrap items-center justify-between gap-2 rounded-md border border-zinc-100 bg-zinc-50 px-3 py-2 text-sm" key={attachment.attachmentId}>
            <div>
              <p className="font-medium text-zinc-900">{attachment.originalFilename}</p>
              <p className="text-xs text-zinc-500">{formatFileSize(attachment.fileSize)}</p>
            </div>
            <button
              className="rounded-md border border-teal-200 bg-teal-50 px-3 py-1 text-xs font-medium text-teal-800 hover:bg-teal-100 disabled:opacity-50"
              disabled={downloadingId === attachment.attachmentId}
              onClick={() => void handleDownload(attachment)}
              type="button"
            >
              {downloadingId === attachment.attachmentId ? msg.ui("common.loading") : msg.ui("common.downloadAttachment")}
            </button>
          </li>
        ))}
      </ul>

      {(attachmentsQuery.data?.length ?? 0) === 0 && !attachmentsQuery.isLoading ? (
        <p className="text-sm text-zinc-500">{msg.ui("common.noAttachments")}</p>
      ) : null}
    </section>
  );
}
