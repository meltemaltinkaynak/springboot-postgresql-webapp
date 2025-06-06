 // Toast bildirimi fonksiyonu
function showToastMessage(message, isSuccess = true) {
    const toast = document.getElementById("toast");
  
   
    if (Array.isArray(message)) {
      toast.innerHTML = message.map(msg => `<div>${msg}</div>`).join("");
    } else {
      toast.innerText = message;
    }
  
    toast.style.backgroundColor = isSuccess ? "#28a745" : "#dc3545"; 
    toast.classList.add("show");
  
    setTimeout(() => {
      toast.classList.remove("show");
    }, 8000); 
  }  
   
   
   document.addEventListener("DOMContentLoaded", async function () {
        const userName = localStorage.getItem("userName") || "Kullanıcı";
        const userSurname = localStorage.getItem("userSurname") || "";
        const userRole = localStorage.getItem("userRole");

        

        // İçerik listesi gösterilecek alanı bul
        const contentContainer = document.querySelector(".content-container");

        // Son bir haftanın içeriklerini al
        async function fetchLastWeekContent() {
            try {
                const response = await fetch("https://localhost:8443/contents/last-week", {
                    method: 'GET',
                    credentials: 'include'
                });
                if (!response.ok) throw new Error("İçerikler yüklenirken hata oluştu");

                const jsonResponse = await response.json();
                const contents = jsonResponse.data;

                displayContents(contents);
            } catch (error) {
                console.error("İçerik yükleme hatası:", error);
            }
        }

        // İçerikleri sayfaya ekle
        async function displayContents(contents) {
            contentContainer.innerHTML = `<h1>Son Eklenen İçerikler </h1>`; // Başlık ekle

            contents.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // Tarihe göre sırala (en yeni en üstte)

            for (const content of contents) {
                const author = await fetchAuthor(content.userId);  // İçeriğin yazarını al
                const commentsCount = await fetchCommentCount(content.contentId);
                const likesCount = await fetchLikeCount(content.contentId);

                const contentCard = document.createElement("div");
                contentCard.classList.add("content-card");
                contentCard.dataset.contentId = content.contentId; // İçerik ID'sini veri olarak ekle

                const baseUrl = "https://localhost:8443";
                const imageUrl = content.photo ? `${baseUrl}${content.photo}` : "images/default.jpg";




                contentCard.innerHTML = `
            
                            <h2 class="content-title"><a href="content-detail.html?postId=${content.contentId}">${content.title}</a></h2>
                        
                            <img src="${imageUrl}" alt="İçerik Görseli" class="content-image">
                            <p class="content-text">${content.text}</p>
                            <p class="content-category">Kategori: <span>${content.category}</span></p>
                            <p class="content-author">Yazar: <span>${author}</span></p> <!-- İçerik yazarı -->
                            <p class="content-date">Tarih: ${new Date(content.createdAt).toLocaleDateString()}</p>
                        

                            <div class="content-actions">
                                <button class="like-btn">
                                    <i class="fas fa-thumbs-up"></i> <span class="like-count">${likesCount}</span>
                                </button>
                                <button class="comment-btn">
                                    <i class="fas fa-comment"></i> <span class="comment-count">${commentsCount}</span>
                                </button>
                            </div>

                            
                            <div class="content-likes">
                                <a href="#" class="view-likes">Beğeniler</a>
                            </div>

                             <!-- Yorumları Gör Linki -->
                            <div class="view-comments-section">
                                <a href="#" class="view-comments-link">Yorumlar</a>
                            </div>

                            

                            <div class="comments-section" style="display: none;">
                                <h3>Yorumlar:</h3>
                                <div class="comments-container"></div>
                                <textarea class="comment-input" placeholder="Yorumunuzu yazın..."></textarea>
                                <button class="submit-comment">Gönder</button>
                            </div>

                            <div class="likes-section" style="display: none;">
                                <h3>Beğeniler:</h3>
                                <div class="likes-container"></div> <!-- Beğeni bölümü -->
                            </div>
                        `;
                        contentContainer.appendChild(contentCard);

                        // Yorum yapma
                        const submitCommentButton = contentCard.querySelector(".submit-comment");
                        const commentInput = contentCard.querySelector(".comment-input");

                        submitCommentButton.addEventListener("click", async function () {
                            const commentText = commentInput.value.trim();
                            if (commentText === "") {
                                showToastMessage("Lütfen bir yorum girin!", false);
                                return;
                            }

                            await submitComment(content.contentId, commentText);
                            commentInput.value = ""; // Yorum kutusunu temizle

                            // Yorumlar güncellensin
                            const comments = await fetchComments(content.contentId);
                            loadComments(comments, commentsSection.querySelector(".comments-container"));
                        });

                        // Yorumları Gör Linki tıklanınca yorumları göster
                        const viewCommentsLink = contentCard.querySelector(".view-comments-link");
                        const commentsSection = contentCard.querySelector(".comments-section");
                        const commentButton = contentCard.querySelector(".comment-btn");

                        viewCommentsLink.addEventListener("click", async function (event) {
                            event.preventDefault();
                            // Yorumları ve yorum yapma alanını aç
                            commentsSection.style.display = "block";
                            const comments = await fetchComments(content.contentId);
                            loadComments(comments, commentsSection.querySelector(".comments-container"));
                        });

                        // Yorum Butonuna tıklanınca sadece yorumları göster (yorum yapma kısmı yok)
                        commentButton.addEventListener("click", async function () {
                            if (commentsSection.style.display === "none") {
                                commentsSection.style.display = "block";
                                const comments = await fetchComments(content.contentId);
                                loadComments(comments, commentsSection.querySelector(".comments-container"));
                            } else {
                                commentsSection.style.display = "none";
                            }
                        });

                        

                    

                    
                       // Beğeni linkine tıklandığında beğeni listesini göster
                        const likeLink = contentCard.querySelector(".view-likes");
                        const likesSection = contentCard.querySelector(".likes-section");

                        likeLink.addEventListener("click", async function (event) {
                            event.preventDefault();
                            if (likesSection.style.display === "none" || likesSection.style.display === "") {
                                likesSection.style.display = "block";
                                const likes = await fetchLikes(content.contentId);
                                loadLikes(likes, likesSection.querySelector(".likes-container"));
                            } else {
                                likesSection.style.display = "none";
                            }
                        });
                    }
                

            

            
        }

        // Yazar'ı al (içerik yazarı)
        async function fetchAuthor(userId) {
            try {
                const response = await fetch(`https://localhost:8443/users/author/${userId}`, {
                    method: 'GET',
                    credentials: 'include'
                });
                const jsonResponse = await response.json();
                return jsonResponse.data ? `${jsonResponse.data.firstName} ${jsonResponse.data.lastName}` : "Yazar Bulunamadı";
            } catch (error) {
                console.error("Yazar yükleme hatası:", error);
                return "Bilinmiyor";  
            }
        }

        async function fetchComments(contentId) {
            try {
                const response = await fetch(`https://localhost:8443/comments/list/${contentId}`, {
                    method: 'GET',
                    credentials: 'include'
                });

                if (!response.ok) {
                    throw new Error("Yorumlar yüklenirken hata oluştu: " + response.statusText);
                }

                const jsonResponse = await response.json();
                return jsonResponse.data || []; 
            } catch (error) {
                console.error("Yorum yükleme hatası:", error);
                return [];
            }
        }

        // Yorumları yükle ve göster
        async function loadComments(comments, commentsContainer) {
            commentsContainer.innerHTML = "";  // Eski yorumları temizle

            for (const comment of comments) {
                const commenterName = await fetchCommenterName(comment.userId);  // Yorum yapan kişinin adı

                const commentCard = document.createElement("div");
                commentCard.classList.add("comment-card");
                commentCard.innerHTML = `
                    <p class="comment-author">${commenterName}</p>
                    <p class="comment-text">${comment.text}</p>
                    <p class="comment-date">${new Date(comment.createdAt).toLocaleDateString()}</p>
                    
                `;
                commentsContainer.appendChild(commentCard);
            }
        }

        // Yorum yapan kişinin adını al
        async function fetchCommenterName(userId) {
            try {
                const response = await fetch(`https://localhost:8443/users/author/${userId}`, {
                    method: 'GET',
                    credentials: 'include'
                });
                const jsonResponse = await response.json();
                return jsonResponse.data ? `${jsonResponse.data.firstName} ${jsonResponse.data.lastName}` : "Bilinmiyor"; 
            } catch (error) {
                console.error("Yorum yapan kişi bilgisi yüklenirken hata oluştu:", error);
                return "Bilinmiyor";
            }
        }

        // Yorum sayısını al
        async function fetchCommentCount(contentId) {
            try {
                const response = await fetch(`https://localhost:8443/comments/${contentId}/comment-count`, {
                    method: 'GET',
                    credentials: 'include'
                });
                if (!response.ok) {
                    console.error("Yorum sayısı yüklenirken hata oluştu:", response.statusText);
                    return 0;
                }
                const jsonResponse = await response.json();
                return jsonResponse.data || 0;
            } catch (error) {
                console.error("Yorum sayısı yüklenirken hata oluştu:", error);
                return 0;
            }
        }


        //yorum yapma
        async function submitComment(contentId, commentText) {
            try {
               
        
                const response = await fetch(`https://localhost:8443/comments/${contentId}`, {
                    method: "POST",
                    credentials: "include",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ contentId, text: commentText })
                });
        
                const result = await response.json();

            
        
                //API isteği başarısız olduysa
                if (!response.ok) {
                    // Backend'den gelen hata mesajını göster
                    const errorMessage = result.data?.description || result.errorMessage || "Beğeni işlemi başarısız!";
                    showToastMessage(errorMessage, false);
                    return;
                }
               
                showToastMessage("Yorum başarıyla eklendi!", true);
        
                
        
                // Yorum sayısını güncelle
                const updatedCommentCount = await fetchCommentCount(contentId);
                const commentButton = document.querySelector(`[data-content-id="${contentId}"] .comment-btn .comment-count`);
                if (commentButton) {
                    commentButton.textContent = updatedCommentCount;
                }
        
                // Yorumları tekrar yükle
                const commentsContainer = document.querySelector(`[data-content-id="${contentId}"] .comments-container`);
                if (commentsContainer) {
                    const comments = await fetchComments(contentId);
                    loadComments(comments, commentsContainer);
                }
        
            } catch (error) {
                console.error("Yorum eklenirken hata oluştu:", error.message);
                showToastMessage(error.message, false);
            }
        }


        // Beğeni sayısını al
        async function fetchLikeCount(contentId) {
            try {
                const response = await fetch(`https://localhost:8443/like/${contentId}/like-count`, {
                    method: 'GET',
                    credentials: 'include'
                });
                if (!response.ok) {
                    console.error("Beğeni sayısı yüklenirken hata oluştu:", response.statusText);
                    return 0;
                }
                const jsonResponse = await response.json();
                return jsonResponse.data || 0;
            } catch (error) {
                console.error("Beğeni sayısı yüklenirken hata oluştu:", error);
                return 0;
            }
        }

        /// Beğeni listesi al
        async function fetchLikes(contentId) {
            try {
                const response = await fetch(`https://localhost:8443/like/${contentId}`, {
                    method: 'GET',
                    credentials: 'include'
                });

                if (!response.ok) {
                    throw new Error("Beğeniler yüklenirken hata oluştu: " + response.statusText);
                }

                const jsonResponse = await response.json();
                return jsonResponse.data || []; // Eğer data yoksa boş dizi dön
            } catch (error) {
                console.error("Beğeni yükleme hatası:", error);
                return [];
            }
        }

        
        // Beğenileri yükle ve göster
        async function loadLikes(likes, likesContainer) {
            likesContainer.innerHTML = "";  // Eski beğenileri temizle

            for (const like of likes) {
                const likerName = await fetchUserName(like.userId);  // Beğenen kişinin adı

                const likeCard = document.createElement("div");
                likeCard.classList.add("like-card");
                likeCard.innerHTML = `
                    <p class="like-author">${likerName}</p>
                    <p class="like-time">${new Date(like.createdAt).toLocaleDateString()}</p>
                `;
                likesContainer.appendChild(likeCard);
            }
        }

        // Kullanıcı adını almak için fonksiyon
        async function fetchUserName(userId) {
            try {
                const response = await fetch(`https://localhost:8443/users/author/${userId}`, {
                    method: 'GET',
                    credentials: 'include'
                });
                if (!response.ok) {
                    throw new Error('Kullanıcı adı alınamadı');
                }
                const jsonResponse = await response.json();
                return `${jsonResponse.data.firstName} ${jsonResponse.data.lastName}`; // Kullanıcı adını döndür
            } catch (error) {
                console.error("Kullanıcı adı yüklenirken hata oluştu:", error);
                return "Bilinmiyor";  
            }
        }

        

        
        fetchLastWeekContent();
    });