"use client";

import { useRef, useState } from "react";
import LoadingSpinner from "@/components/LoadingSpinner";
import PaymentStatus from "@/components/PaymentStatus";
import { useRazorpay } from "@/hooks/useRazorpay";
import { paymentService } from "@/services/paymentService";
import type { PaymentResult, PaymentUiState } from "@/types/payment";

const PAYMENT_AMOUNT = 100; // ₹1 in paise
const CURRENCY = "INR";

export default function PaymentButton() {
  const { isLoaded, error: razorpayLoadError, openCheckout } = useRazorpay();
  const [uiState, setUiState] = useState<PaymentUiState>("idle");
  const [result, setResult] = useState<PaymentResult | undefined>();
  const paymentCompletedRef = useRef(false);

  const handlePay = async () => {
    if (!isLoaded) {
      setUiState("failed");
      setResult({ message: "Razorpay checkout is still loading. Please wait." });
      return;
    }

    setUiState("loading");
    setResult(undefined);
    paymentCompletedRef.current = false;

    try {
      const order = await paymentService.createOrder({
        amount: PAYMENT_AMOUNT,
        currency: CURRENCY,
        notes: "Demo payment of ₹1",
      });

      openCheckout({
        key: order.keyId,
        amount: order.amount,
        currency: order.currency,
        order_id: order.orderId,
        name: "Razorpay Demo Store",
        description: "Payment for order " + order.receipt,
        theme: {
          color: "#3395FF",
        },
        handler: async (response) => {
          paymentCompletedRef.current = true;
          setUiState("loading");
          try {
            const verification = await paymentService.verifyPayment({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            });

            setUiState("success");
            setResult({
              orderId: verification.orderId,
              paymentId: verification.paymentId,
              message: verification.message,
            });
          } catch {
            setUiState("failed");
            setResult({
              message: "Payment completed but verification failed on the server.",
            });
          }
        },
        modal: {
          ondismiss: () => {
            if (paymentCompletedRef.current) {
              return;
            }
            setUiState("failed");
            setResult({ message: "Payment was cancelled." });
          },
        },
      });

      setUiState("idle");
    } catch {
      setUiState("failed");
      setResult({
        message: "Failed to create order. Check backend connection and Razorpay keys.",
      });
    }
  };

  const isDisabled = uiState === "loading" || !isLoaded;

  return (
    <div className="w-full max-w-md">
      {razorpayLoadError && (
        <p className="mb-4 rounded-lg bg-amber-50 px-4 py-3 text-sm text-amber-800">
          {razorpayLoadError}
        </p>
      )}

      <button
        type="button"
        onClick={handlePay}
        disabled={isDisabled}
        className="w-full rounded-xl bg-razorpay px-6 py-4 text-lg font-semibold text-white shadow-lg transition hover:bg-blue-600 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {uiState === "loading" ? "Please wait..." : "Pay ₹1"}
      </button>

      {uiState === "loading" && (
        <LoadingSpinner message="Creating order and opening checkout..." />
      )}

      <PaymentStatus state={uiState} result={result} />
    </div>
  );
}
