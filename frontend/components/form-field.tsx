type FormFieldProps = {
  label: string;
  htmlFor: string;
  required?: boolean;
  children: React.ReactNode;
  error?: string;
};

export function FormField({ label, htmlFor, required, children, error }: FormFieldProps) {
  return (
    <div className="space-y-1">
      <label className="block text-sm font-medium text-zinc-700" htmlFor={htmlFor}>
        {label}
        {required ? <span className="text-red-600"> *</span> : null}
      </label>
      {children}
      {error ? <p className="text-sm text-red-600">{error}</p> : null}
    </div>
  );
}
