function showToastMessage(message, isSuccess = true) {
  const toast = document.getElementById("toast");

 
  const image = document.createElement("img");
  image.src = isSuccess ? "images/ok.png" : "images/x.png";

  // Mesajları düzenleme (birden fazla eleman varsa madde işareti ekle, yoksa olduğu gibi bırak)
  let formattedMessage;
  if (Array.isArray(message) && message.length > 1) {
    formattedMessage = message.map(msg => `• ${msg}`).join("<br>");
  } else {
    formattedMessage = Array.isArray(message) ? message[0] : message;
  }

  // Önce içeriği temizle
  toast.innerHTML = "";

  // Görseli ekle
  toast.appendChild(image);

  // Mesajı ekle
  const messageContainer = document.createElement("div");
  messageContainer.innerHTML = formattedMessage;
  toast.appendChild(messageContainer);

  // İlgili class'ları ekle veya çıkar
  toast.classList.toggle("success", isSuccess);
  toast.classList.toggle("error", !isSuccess);

  toast.classList.add("show");

  setTimeout(() => {
    toast.classList.remove("show");
  }, 8000); // 8 saniye sonra kaybolacak
}