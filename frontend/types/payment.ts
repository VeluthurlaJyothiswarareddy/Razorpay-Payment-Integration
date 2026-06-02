export type OrderStatus = "CREATED" | "PAID" | "FAILED" | "EXPIRED";

export type PaymentStatus =
  | "CREATED"
  | "AUTHORIZED"
  | "CAPTURED"
  | "FAILED"
  | "REFUNDED";

export interface CreateOrderRequest {
  amount: number;
  currency: string;
  receipt?: string;
  notes?: string;
}

export interface CreateOrderResponse {
  orderId: string;
  amount: number;
  currency: string;
  status: OrderStatus;
  keyId: string;
  receipt: string;
  createdAt: string;
}

export interface VerifyPaymentRequest {
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
}

export interface VerifyPaymentResponse {
  verified: boolean;
  paymentId: string;
  orderId: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  message: string;
  verifiedAt: string;
}

export interface RazorpaySuccessResponse {
  razorpay_payment_id: string;
  razorpay_order_id: string;
  razorpay_signature: string;
}

export type PaymentUiState = "idle" | "loading" | "success" | "failed";

export interface PaymentResult {
  orderId?: string;
  paymentId?: string;
  message?: string;
}
