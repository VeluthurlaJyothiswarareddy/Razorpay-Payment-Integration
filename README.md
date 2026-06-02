# Razorpay Payment Integration

Full-stack monorepo with a **Spring Boot 3** backend and **Next.js 15** frontend for Razorpay test payments.

## Project Structure

```
razporpay_int/
├── backend/          # Spring Boot 3 + MongoDB + Razorpay Java SDK
├── frontend/         # Next.js 15 App Router + TailwindCSS
└── README.md
```

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+
- MongoDB running locally on `mongodb://localhost:27017`
- Razorpay Test API keys from [Razorpay Dashboard](https://dashboard.razorpay.com/)

## Backend Setup

### 1. Configure Razorpay credentials

Edit `backend/src/main/resources/application.yml` or set environment variables:

```bash
export RAZORPAY_KEY_ID=rzp_test_xxxxx
export RAZORPAY_KEY_SECRET=your_key_secret
export RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

### 2. Start MongoDB

```bash
# Using Docker
docker run -d -p 27017:27017 --name mongodb mongo:7
```

### 3. Run the backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs at **http://localhost:8080**

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/create-order` | Create Razorpay order |
| POST | `/api/payments/verify` | Verify payment signature (HMAC SHA256) |
| POST | `/api/payments/webhook` | Handle Razorpay webhooks |
| GET | `/api/payments/{paymentId}` | Get payment by ID |
| GET | `/api/orders/{orderId}` | Get order by ID |

**Swagger UI:** http://localhost:8080/swagger-ui.html

### Backend Architecture

```
controller/   → REST endpoints
service/      → RazorpayOrderService, PaymentVerificationService, WebhookService
repository/   → MongoRepository interfaces
entity/       → OrderDocument, PaymentDocument (orders & payments collections)
dto/          → Request/response objects
config/       → Razorpay client, CORS, OpenAPI
exception/    → Global exception handling
```

### Webhook Events Handled

- `payment.captured`
- `payment.failed`
- `order.paid`
- `refund.created`

Configure webhook URL in Razorpay Dashboard:
`http://your-domain/api/payments/webhook`

## Frontend Setup

### 1. Install dependencies

```bash
cd frontend
npm install
```

### 2. Configure environment

```bash
cp .env.local.example .env.local
```

Set `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080`

### 3. Run the frontend

```bash
npm run dev
```

Frontend runs at **http://localhost:3000**

### Frontend Architecture

```
app/          → Pages (App Router)
components/   → PaymentButton, PaymentStatus, LoadingSpinner
services/     → paymentService.ts (Axios API calls)
types/        → TypeScript interfaces
hooks/        → useRazorpay (dynamic script loading)
```

### Payment Flow

1. User clicks **Pay ₹1**
2. Frontend calls `POST /api/payments/create-order`
3. Backend creates Razorpay order via Java SDK and saves to MongoDB
4. Razorpay Checkout opens with order details
5. On success, frontend calls `POST /api/payments/verify`
6. Backend verifies HMAC SHA256 signature and updates status

## Test Payment

Use Razorpay test card details:

- Card: `4111 1111 1111 1111`
- Expiry: any future date
- CVV: any 3 digits

## License

MIT
