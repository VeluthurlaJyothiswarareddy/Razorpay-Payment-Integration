import axios from "axios";
import type {
  CreateOrderRequest,
  CreateOrderResponse,
  VerifyPaymentRequest,
  VerifyPaymentResponse,
} from "@/types/payment";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const paymentService = {
  createOrder: async (
    payload: CreateOrderRequest
  ): Promise<CreateOrderResponse> => {
    const { data } = await apiClient.post<CreateOrderResponse>(
      "/api/payments/create-order",
      payload
    );
    return data;
  },

  verifyPayment: async (
    payload: VerifyPaymentRequest
  ): Promise<VerifyPaymentResponse> => {
    const { data } = await apiClient.post<VerifyPaymentResponse>(
      "/api/payments/verify",
      payload
    );
    return data;
  },
};

export default paymentService;
