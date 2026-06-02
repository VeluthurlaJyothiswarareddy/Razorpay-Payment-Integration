interface LoadingSpinnerProps {
  message?: string;
}

export default function LoadingSpinner({
  message = "Processing payment...",
}: LoadingSpinnerProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-4 py-8">
      <div
        className="h-10 w-10 animate-spin rounded-full border-4 border-razorpay border-t-transparent"
        role="status"
        aria-label="Loading"
      />
      <p className="text-sm text-slate-600">{message}</p>
    </div>
  );
}
