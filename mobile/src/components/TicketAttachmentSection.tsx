import { useEffect, useState } from "react";
import { Linking, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import * as DocumentPicker from "expo-document-picker";
import {
  fetchAttachmentDownloadUrl,
  fetchTicketAttachments,
  uploadCustomerTicketAttachment
} from "@/api/attachments";
import { ApiError } from "@/api/client";
import type { Attachment } from "@/api/types";
import { ErrorMessage } from "@/components/ErrorMessage";
import { msg } from "@/messages";

type TicketAttachmentSectionProps = {
  ticketId: number;
  canUpload: boolean;
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

export function TicketAttachmentSection({ ticketId, canUpload }: TicketAttachmentSectionProps) {
  const [attachments, setAttachments] = useState<Attachment[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function loadAttachments() {
    setLoading(true);
    setErrorMessage(null);
    try {
      const items = await fetchTicketAttachments(ticketId);
      setAttachments(items);
    } catch (error) {
      setAttachments([]);
      setErrorMessage(error instanceof ApiError ? error.message : msg.ui("customer.loadAttachmentsFailed"));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadAttachments();
  }, [ticketId]);

  async function pickAndUpload() {
    setErrorMessage(null);
    const result = await DocumentPicker.getDocumentAsync({
      copyToCacheDirectory: true,
      multiple: false
    });

    if (result.canceled || !result.assets?.[0]) {
      return;
    }

    const asset = result.assets[0];
    setUploading(true);
    try {
      await uploadCustomerTicketAttachment(ticketId, {
        uri: asset.uri,
        name: asset.name,
        mimeType: asset.mimeType
      });
      await loadAttachments();
    } catch (error) {
      setErrorMessage(error instanceof ApiError ? error.message : msg.ui("customer.uploadAttachmentFailed"));
    } finally {
      setUploading(false);
    }
  }

  async function downloadAttachment(attachment: Attachment) {
    setErrorMessage(null);
    try {
      const result = await fetchAttachmentDownloadUrl(attachment.attachmentId);
      await Linking.openURL(result.downloadUrl);
    } catch (error) {
      setErrorMessage(error instanceof ApiError ? error.message : msg.ui("customer.downloadAttachmentFailed"));
    }
  }

  return (
    <View style={styles.section}>
      <View style={styles.headerRow}>
        <Text style={styles.sectionTitle}>{msg.ui("common.attachment")}</Text>
        {canUpload ? (
          <TouchableOpacity disabled={uploading} onPress={() => void pickAndUpload()} style={styles.uploadButton}>
            <Text style={styles.uploadButtonText}>
              {uploading ? msg.ui("common.uploadingAttachment") : msg.ui("common.uploadAttachment")}
            </Text>
          </TouchableOpacity>
        ) : null}
      </View>
      {errorMessage ? <ErrorMessage message={errorMessage} /> : null}
      {loading ? <Text style={styles.hint}>{msg.ui("common.loading")}</Text> : null}
      {attachments.map((attachment) => (
        <View key={attachment.attachmentId} style={styles.item}>
          <View style={styles.itemText}>
            <Text style={styles.fileName}>{attachment.originalFilename}</Text>
            <Text style={styles.fileMeta}>{formatFileSize(attachment.fileSize)}</Text>
          </View>
          <TouchableOpacity onPress={() => void downloadAttachment(attachment)} style={styles.downloadButton}>
            <Text style={styles.downloadButtonText}>{msg.ui("common.downloadAttachment")}</Text>
          </TouchableOpacity>
        </View>
      ))}
      {!loading && attachments.length === 0 ? <Text style={styles.hint}>{msg.ui("common.noAttachments")}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  section: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 12,
    borderWidth: 1,
    gap: 10,
    padding: 16
  },
  headerRow: {
    alignItems: "center",
    flexDirection: "row",
    justifyContent: "space-between"
  },
  sectionTitle: {
    color: "#09090b",
    fontSize: 16,
    fontWeight: "900"
  },
  uploadButton: {
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 8,
    borderWidth: 1,
    paddingHorizontal: 10,
    paddingVertical: 8
  },
  uploadButtonText: {
    color: "#27272a",
    fontSize: 12,
    fontWeight: "800"
  },
  item: {
    alignItems: "center",
    borderTopColor: "#f4f4f5",
    borderTopWidth: 1,
    flexDirection: "row",
    gap: 10,
    justifyContent: "space-between",
    paddingTop: 10
  },
  itemText: {
    flex: 1,
    gap: 2
  },
  fileName: {
    color: "#09090b",
    fontSize: 14,
    fontWeight: "700"
  },
  fileMeta: {
    color: "#71717a",
    fontSize: 12
  },
  downloadButton: {
    backgroundColor: "#ecfdf5",
    borderColor: "#0f766e",
    borderRadius: 8,
    borderWidth: 1,
    paddingHorizontal: 10,
    paddingVertical: 8
  },
  downloadButtonText: {
    color: "#0f766e",
    fontSize: 12,
    fontWeight: "800"
  },
  hint: {
    color: "#71717a",
    fontSize: 14
  }
});
