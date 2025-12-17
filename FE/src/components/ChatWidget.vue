<script setup>
import { ref } from "vue";

const isOpen = ref(false);
const message = ref("");
const messages = ref([
  { sender: "bot", text: "Xin chào 👋! Tôi là AI, bạn muốn hỏi gì nào?" },
]);

const toggleChat = () => {
  isOpen.value = !isOpen.value;
};

const sendMessage = async () => {
  if (!message.value.trim()) return;

  const userMsg = message.value;
  messages.value.push({ sender: "user", text: userMsg });
  message.value = "";

  messages.value.push({
    sender: "bot",
    text: "⏳ Đang nghĩ câu trả lời...",
  });

  try {
    const res = await fetch("http://localhost:8080/api/chat", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        messages: [{ role: "user", content: userMsg }],
      }),
    });

    const data = await res.json();
    messages.value.pop();

    let reply = "";

    data.results.forEach((r) => {
      if (r.products) {
        r.products.forEach((p) => {
          reply += `
            <div class="product">
              <b>${p.ten}</b><br>
              <img src="${p.hinhAnhUrl}" />
              Giá: ${p.gia}<br>
              Màu: ${p.mauSac}<br>
              Size: ${p.kichThuoc}<br>
              Chất liệu: ${p.chatLieu}<br>
              Thương hiệu: ${p.thuongHieu}<br>
              Danh mục: ${p.danhMuc}<br><br>
            </div>
          `;
        });
      }
    });

    messages.value.push({
      sender: "bot",
      text: reply,
    });
  } catch (err) {
    messages.value.pop();
    messages.value.push({
      sender: "bot",
      text: "⚠️ Lỗi khi gọi API",
    });
  }
};
</script>

<template>
  <button class="chat-toggle" @click="toggleChat">💬</button>

  <div v-if="isOpen" class="chat-box">
    <div class="chat-header">
      <span>🤖 Chat AI</span>
      <button @click="toggleChat">✖</button>
    </div>

    <div class="chat-body">
      <div
        v-for="(m, index) in messages"
        :key="index"
        :class="['msg', m.sender]"
        v-html="m.text"
      ></div>
    </div>

      <div class="chat-footer">
        <input
          v-model="message"
          @keyup.enter="sendMessage"
          placeholder="Nhập tin nhắn..."
        />
        <button @click="sendMessage">Gửi</button>
      </div>
  </div>
</template>

<style scoped>
.chat-toggle {
  position: fixed;
  bottom: 20px;
  right: 25px;
  background: #ffc107;
  border: none;
  padding: 12px;
  border-radius: 50%;
}

.chat-box {
  position: fixed;
  bottom: 80px;
  right: 25px;
  width: 320px;
  border: 2px solid #ffc107;
  border-radius: 12px;
  background: white;
}

.chat-header {
  background: #ffc107;
  padding: 10px;
  display: flex;
  justify-content: space-between;
}

.chat-body {
  max-height: 300px;
  overflow-y: auto;
  padding: 10px;
}

.msg {
  background: #f0f0f0;
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 8px;
}

.msg.user {
  background: #ffc107;
  text-align: right;
}

/* 🔥 QUAN TRỌNG NHẤT */
:deep(.msg img) {
  display: block;
  width: 120px;
  max-width: 100%;
  margin: 6px 0;
  border-radius: 6px;
}

.chat-footer {
  display: flex;
  border-top: 1px solid #ddd;
}

.chat-footer input {
  flex: 1;
  border: none;
  padding: 8px;
}
</style>
