document.addEventListener("DOMContentLoaded", async function () {
    try {
        
        const urlParams = new URLSearchParams(window.location.search);
        const category = urlParams.get('category'); 

        if (!category) {
            console.error("Kategori parametresi URL'den alınamadı.");
            return;
        }

       
        const categoryTitle = document.getElementById("category-title");

        
        const categoryMap = {
            "webdesign": "Web Tasarım",
            "html": "HTML ",
            "css": "CSS",
            "javascript": "Javascript ",
            "backendteknolojileri": "Backend Teknolojileri",
            "veritabani": "Veritabanı Yönetimi",
            "api": "API Geliştirme",
            "hosting": "Sunucu ve Hosting",
            "uiux": "UI - UX",
            "tasarimaraclari": "Tasarım Araçları",
            "webperformans": "Web Performans ve SEO",
            "webguvenlik": "Web Güvenliği"
        };

        const formattedCategory = category
            .split("-")
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(" ");

        if (categoryTitle) {
            categoryTitle.textContent = `Kategori: ${categoryMap[category] || formattedCategory}`;
        }

       
        const response = await fetch("https://localhost:8443/auth/me", {
            method: "GET",
            credentials: "include",
        });

        if (!response.ok) {
            throw new Error(`HTTP hata! Durum: ${response.status}`);
        }

        const data = await response.json();

        const contentContainer = document.querySelector(".content-container");

        async function displayContents(contents) {
            contents.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            for (const content of contents) {
                const author = await fetchAuthor(content.userId);

                const contentCard = document.createElement("div");
                contentCard.classList.add("content-card");
                contentCard.dataset.contentId = content.contentId;

                const baseUrl = "https://localhost:8443";
                const imageUrl = content.photo ? `${baseUrl}${content.photo}` : "images/default.jpg";
                const restrictedLabel = content.restricted ? " | Kısıtlı içerik" : "";

                contentCard.innerHTML = `
                    <img src="${imageUrl}" alt="İçerik Görseli" class="content-image">
                    <h2 class="content-title"><a href="content-detail.html?postId=${content.contentId}">${content.title}</a></h2>
                    <p class="content-text">${content.text}</p>
                    <p class="content-meta">
                        ${new Date(content.createdAt).toLocaleDateString()} ${restrictedLabel}
                        <span class="content-author">${author}</span>
                    </p>
                `;

                contentContainer.appendChild(contentCard);
            }
        }

        async function fetchAuthor(userId) {
            try {
                const response = await fetch(`https://localhost:8443/users/public/author/${userId}`, {
                    method: 'GET',
                    credentials: 'include'
                });
                const jsonResponse = await response.json();
                return jsonResponse.data
                    ? `${jsonResponse.data.firstName} ${jsonResponse.data.lastName}`
                    : "Yazar Bulunamadı";
            } catch (error) {
                console.error("Yazar yükleme hatası:", error);
                return "Bilinmiyor";
            }
        }

        if (data && data.result && data.data) {
            const user = data.data;

            async function fetchContent(category) {
                try {
                    const response = await fetch(`https://localhost:8443/contents/${category}`, {
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

            await fetchContent(category);
        } else {
            async function fetchPublicContent(category) {
                try {
                    const response = await fetch(`https://localhost:8443/contents/public/${category}`, {
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

            await fetchPublicContent(category);
        }

    } catch (error) {
        console.error("Kullanıcı bilgisi alınamadı:", error);
    }
});
