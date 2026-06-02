import type { PaymentResult, PaymentUiState } from "@/types/payment";

interface PaymentStatusProps {
  state: PaymentUiState;
  result?: PaymentResult;
}

export default function PaymentStatus({ state, result }: PaymentStatusProps) {
  if (state === "idle" || state === "loading") {
    return null;
  }

  if (state === "success") {
    return (
      <div className="mt-6 rounded-xl border border-emerald-200 bg-emerald-50 p-6 text-center">
        <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-full bg-emerald-100 text-2xl">
          ✓
        </div>
        <h2 className="text-lg font-semibold text-emerald-800">Payment Success</h2>
        <p className="mt-2 text-sm text-emerald-700">
          {result?.message ?? "Your payment was verified successfully."}
        </p>
        {result?.paymentId && (
          <p className="mt-3 text-xs text-emerald-600">
            Payment ID: <span className="font-mono">{result.paymentId}</span>
          </p>
        )}
        {result?.orderId && (
          <p className="mt-1 text-xs text-emerald-600">
            Order ID: <span className="font-mono">{result.orderId}</span>
          </p>
        )}
      </div>
    );
  }

  return (
    <div className="mt-6 rounded-xl border border-red-200 bg-red-50 p-6 text-center">
      <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-full bg-red-100 text-2xl">
        ✕
      </div>
      <h2 className="text-lg font-semibold text-red-800">Payment Failed</h2>
      <p className="mt-2 text-sm text-red-700">
        {result?.message ?? "Something went wrong. Please try again."}
      </p>
    </div>
  );
}
