// Custom thông báo

import { getCurrentInstance } from "vue";

export function useNotify() {
  const app = getCurrentInstance();
  const notifyRef = app.appContext.config.globalProperties.$notifyRef;

  const show = (msg, type = "info") => {
    if (notifyRef) notifyRef.addToast(msg, type);
  };

  return {
    success: (msg) => show(msg, "success"),
    error: (msg) => show(msg, "error"),
    warning: (msg) => show(msg, "warning"),
    info: (msg) => show(msg, "info"),
  };
}
