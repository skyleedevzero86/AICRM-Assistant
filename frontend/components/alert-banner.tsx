type AlertBannerProps = {
  variant: "success" | "error" | "info";
  message: string;
};

const VARIANT_CLASSES: Record<AlertBannerProps["variant"], string> = {
  success: "border-green-200 bg-green-50 text-green-900",
  error: "border-red-200 bg-red-50 text-red-900",
  info: "border-zinc-200 bg-zinc-50 text-zinc-800"
};

export function AlertBanner({ variant, message }: AlertBannerProps) {
  return (
    <div className={`rounded-lg border px-4 py-3 text-sm ${VARIANT_CLASSES[variant]}`} role="alert">
      {message}
    </div>
  );
}
