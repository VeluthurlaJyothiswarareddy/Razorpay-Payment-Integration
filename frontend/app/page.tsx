import PaymentButton from "@/components/PaymentButton";

export default function HomePage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-50 to-blue-50 px-4">
      <div className="w-full max-w-lg rounded-2xl border border-slate-200 bg-white p-8 shadow-xl">
        <div className="mb-8 text-center">
          <p className="text-sm font-medium uppercase tracking-wide text-razorpay">
            Razorpay Integration
          </p>
          <h1 className="mt-2 text-3xl font-bold text-slate-900">
            Secure Payment
          </h1>
          <p className="mt-3 text-slate-600">
            Complete a test payment of ₹1 using Razorpay Checkout.
          </p>
        </div>

        <div className="rounded-xl bg-slate-50 p-4">
          <div className="flex items-center justify-between text-sm">
            <span className="text-slate-600">Amount</span>
            <span className="text-lg font-semibold text-slate-900">₹1.00</span>
          </div>
          <div className="mt-2 flex items-center justify-between text-sm">
            <span className="text-slate-600">Currency</span>
            <span className="font-medium text-slate-900">INR</span>
          </div>
        </div>

        <div className="mt-8 flex justify-center">
          <PaymentButton />
        </div>
      </div>
    </main>
  );
}
