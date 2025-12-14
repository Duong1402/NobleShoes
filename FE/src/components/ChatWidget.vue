<script setup>
import { ref } from "vue";
import { sendChatMessage } from "@/service/KhachHangService";

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

  messages.value.push({ sender: "bot", text: "⏳ Đang nghĩ câu trả lời..." });

  try {
    const payload = {
      model: "meta-llama/llama-3.1-8b-instruct",
      messages: [
        {
          role: "system",
          content: "Bạn là trợ lý AI nói tiếng Việt thân thiện.",
        },
        ...messages.value.map((m) => ({
          role: m.sender === "user" ? "user" : "assistant",
          content: m.text,
        })),
      ],
    };

    const res = await sendChatMessage(payload);

    const data = await res.json();
    messages.value.pop();

    if (data?.choices?.length > 0) {
      messages.value.push({
        sender: "bot",
        text: data.choices[0].message.content.trim(),
      });
    } else {
      messages.value.push({
        sender: "bot",
        text: "⚠️ Xin lỗi, tôi không nhận được phản hồi từ AI.",
      });
    }
  } catch (err) {
    messages.value.pop();
    messages.value.push({
      sender: "bot",
      text: "⚠️ Lỗi khi gọi API: " + err.message,
    });
  }
};
</script>

<template>
  <div>
    <!-- Nút bật chat -->
    <button class="chat-toggle" @click="toggleChat">💬</button>

    <!-- Hộp chat -->
    <div
      v-if="isOpen"
      class="chat-box shadow-lg animate__animated animate__fadeInUp"
    >
      <div class="chat-header">
        <span>🤖 Chat AI</span>
        <button @click="toggleChat" class="close-btn">✖</button>
      </div>

      <div class="chat-body">
        <div
          v-for="(m, index) in messages"
          :key="index"
          :class="['msg', m.sender]"
        >
          {{ m.text }}
        </div>
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
  </div>
</template>

<style scoped>
.chat-toggle {
  position: fixed;
  bottom: 20px;
  right: 25px;
  background-color: #ffc107;
  border: none;
  color: #212529;
  font-size: 22px;
  padding: 12px;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 9999;
  transition: all 0.2s ease;
}

.chat-toggle:hover {
  background-color: #e0a800;
}

.chat-box {
  position: fixed;
  bottom: 80px;
  right: 25px;
  width: 300px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  z-index: 9999;
  border: 2px solid #ffc107;
}

.chat-header {
  background: #ffc107;
  color: #212529;
  padding: 10px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-body {
  max-height: 300px;
  overflow-y: auto;
  padding: 10px;
  background: #fffbe6;
}

.msg {
  margin: 5px 0;
  padding: 8px 10px;
  border-radius: 10px;
  max-width: 80%;
}

.msg.user {
  background: #ffc107;
  color: #212529;
  align-self: flex-end;
  text-align: right;
}

.msg.bot {
  background: #f0f0f0;
  color: #000;
  align-self: flex-start;
}

.chat-footer {
  display: flex;
  border-top: 1px solid #ddd;
}

.chat-footer input {
  flex: 1;
  border: none;
  padding: 8px;
  outline: none;
  background: #fff;
}

.chat-footer button {
  border: none;
  background: #ffc107;
  color: #212529;
  padding: 8px 12px;
  cursor: pointer;
}

.chat-footer button:hover {
  background: #e0a800;
}

.close-btn {
  background: transparent;
  border: none;
  color: #212529;
  cursor: pointer;
  font-size: 16px;
}
</style>
