<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";
import axios from "axios";

const route = useRoute();
const router = useRouter();

const status = ref("");
const txnRef = ref("");

onMounted(async () => {
  status.value = route.query.status;
  txnRef.value = route.query.vnp_TxnRef;

  if (status.value === "success") {
    try {
      await axios.post("http://localhost:8080/admin/vnpay/confirm", {
        txnRef: txnRef.value,
      });

      await Swal.fire({
        icon: "success",
        title: "Thanh toán thành công",
        text: `Mã giao dịch: ${txnRef.value}`,
        confirmButtonText: "OK",
      });
    } catch (e) {
      await Swal.fire({
        icon: "error",
        title: "Lỗi xác nhận thanh toán",
        text: "Không thể cập nhật trạng thái hóa đơn",
      });
    }
  } else if (status.value === "failed") {
    await Swal.fire({
      icon: "error",
      title: "Thanh toán thất bại",
      text: "VNPay từ chối giao dịch",
    });
  } else {
    await Swal.fire({
      icon: "warning",
      title: "Chữ ký không hợp lệ",
      text: "Giao dịch không an toàn",
    });
  }

  router.push("/admin/hoa-don");
});
</script>

<template>
  <div class="payment-result">
    <h2 v-if="status === 'success'">✅ Thanh toán thành công</h2>
    <h2 v-else-if="status === 'failed'">❌ Thanh toán thất bại</h2>
    <h2 v-else>⚠️ Thanh toán không hợp lệ</h2>

    <p>Mã giao dịch: {{ txnRef }}</p>
  </div>
</template>
