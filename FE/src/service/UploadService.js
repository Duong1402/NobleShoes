import axios from "axios";

// Upload trực tiếp lên Cloudinary
export const uploadImageToCloudinary = async (file) => {
  // Chuỗi preset và URL của Cloudinary
  const cloudName = "dsojjxs1n";
  const uploadPreset = "unsigned_preset";
  const url = `https://api.cloudinary.com/v1_1/${cloudName}/image/upload`;

  const formData = new FormData();
  formData.append("file", file);
  formData.append("upload_preset", uploadPreset);

  try {
    const response = await axios.post(url, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    // Trả về URL ảnh sau khi upload thành công
    return response.data.secure_url;
  } catch (err) {
    console.error("Upload thất bại:", err);
    throw err;
  }
};
