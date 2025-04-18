// ContentPanel.js

// URL'den kategori bilgisi alma (örneğin, "/web_tasarimi.html" sayfasında "web_tasarimi" kategorisi)
const category = window.location.pathname.split("/").pop().split(".")[0];

// Backend'den içerikleri almak için fonksiyon
async function fetchContent(category) {
  try {
    const response = await fetch(`/api/content/${category}`); // Backend'den içerik almak için URL
    if (!response.ok) {
      throw new Error("İçerikler alınamadı");
    }
    const data = await response.json();
    displayContent(data); // İçeriği sayfada göster
  } catch (error) {
    console.error("Hata:", error);
  }
}

// İçeriği sayfada göstermek için fonksiyon
function displayContent(contentData) {
  const contentList = document.getElementById("content-list");

  contentData.forEach((content) => {
    const contentCard = document.createElement("div");
    contentCard.classList.add("content-card");

    contentCard.innerHTML = `
      <h2 class="content-title">${content.title}</h2>
      <img src="${content.image}" alt="İçerik Görseli" class="content-image" />
      <p class="content-text">${content.text}</p>
      <p class="content-category">Kategori: <span>${content.category}</span></p>
      <p class="content-author">Yazar: <span>${content.author}</span></p>

      <div class="content-actions">
        <button class="like-btn">
          <i class="fas fa-thumbs-up"></i> <span class="like-count">${content.likes}</span>
        </button>
        <button class="comment-btn">
          <i class="fas fa-comment"></i> <span class="comment-count">${content.comments}</span>
        </button>
      </div>

      <div class="comment-section">
        <h3>Yorumlar</h3>
        <ul class="comment-list">
          <!-- Yorumlar dinamik olarak buraya gelecek -->
        </ul>
        <textarea placeholder="Yorumunuzu yazın..."></textarea>
        <button class="submit-comment">Gönder</button>
      </div>
    `;

    contentList.appendChild(contentCard);
  });
}

// Sayfa yüklendiğinde içerikleri yükle
document.addEventListener("DOMContentLoaded", () => fetchContent(category));
