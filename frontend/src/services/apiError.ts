import axios from 'axios';

interface ApiErrorResponse {
  message?: string;
  fieldErrors?: Record<string, string>;
}

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const data = error.response?.data;
    if (data?.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
      return Object.values(data.fieldErrors)[0];
    }

    if (data?.message) {
      return data.message;
    }
  }

  return fallback;
}
