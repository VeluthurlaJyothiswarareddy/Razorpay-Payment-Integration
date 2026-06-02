"use client";

import { useCallback, useEffect, useState } from "react";

const RAZORPAY_SCRIPT_URL = "https://checkout.razorpay.com/v1/checkout.js";

export function useRazorpay() {
  const [isLoaded, setIsLoaded] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (typeof window === "undefined") {
      return;
    }

    if (window.Razorpay) {
      setIsLoaded(true);
      return;
    }

    const existingScript = document.querySelector<HTMLScriptElement>(
      `script[src="${RAZORPAY_SCRIPT_URL}"]`
    );

    if (existingScript) {
      existingScript.addEventListener("load", () => setIsLoaded(true));
      existingScript.addEventListener("error", () =>
        setError("Failed to load Razorpay checkout script")
      );
      return;
    }

    const script = document.createElement("script");
    script.src = RAZORPAY_SCRIPT_URL;
    script.async = true;
    script.onload = () => setIsLoaded(true);
    script.onerror = () => setError("Failed to load Razorpay checkout script");
    document.body.appendChild(script);
  }, []);

  const openCheckout = useCallback(
    (options: ConstructorParameters<NonNullable<typeof window.Razorpay>>[0]) => {
      if (!window.Razorpay) {
        throw new Error("Razorpay SDK is not loaded yet");
      }

      const razorpay = new window.Razorpay(options);
      razorpay.open();
      return razorpay;
    },
    []
  );

  return { isLoaded, error, openCheckout };
}
