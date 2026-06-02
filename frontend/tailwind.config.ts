import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        razorpay: {
          DEFAULT: "#3395FF",
          dark: "#0C2451",
        },
      },
    },
  },
  plugins: [],
};

export default config;
