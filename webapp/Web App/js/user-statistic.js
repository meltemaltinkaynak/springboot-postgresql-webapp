document.addEventListener("DOMContentLoaded", async function () {
    try {
        // Kullanıcı bilgilerini al
        const response = await fetch("https://localhost:8443/auth/me", {
            method: "GET",
            credentials: "include", 
        });

        if (!response.ok) {
            throw new Error(`HTTP hata! Durum: ${response.status}`);
        }

        const data = await response.json();

        if (data && data.result && data.data) {
            const user = data.data;

            const statisticsContainer = document.querySelector("#statistics");

            // Kullanıcı istatistiklerini getir
            async function fetchUserStatistics() {
                try {
                    const response = await fetch("https://localhost:8443/contents/statistics", {
                        method: "GET",
                        credentials: "include"
                    });

                    if (!response.ok) {
                        throw new Error("İstatistikler yüklenirken hata oluştu");
                    }

                    const result = await response.json();
                    const stats = result.data;

                    displayStatistics(stats);

                } catch (error) {
                    console.error("İstatistik yükleme hatası:", error);
                    statisticsContainer.innerHTML = "<p>İstatistikler yüklenemedi.</p>";
                }
            }

            // İstatistikleri sayfaya ekle
            function displayStatistics(stats) {
                if (!stats) {
                    const noStatsMessage = document.createElement("p");
                    noStatsMessage.innerText = "İstatistik verileri bulunamadı.";
                    statisticsContainer.appendChild(noStatsMessage);
                    return;
                }
                // Diğer istatistik kartları - Yatay satır
                const statRow = document.createElement("div");
                statRow.classList.add("stat-row");
            
                const totalContentCard = createStatCard("Toplam İçerik Sayısı", stats.totalContentCount);
                const restrictedCard = createStatCard("Kısıtlı İçerik Sayısı", stats.restrictedContentCount);
                const unrestrictedCount = stats.totalContentCount - stats.restrictedContentCount;
                const unrestrictedCard = createStatCard("Kısıtsız İçerik Sayısı", unrestrictedCount);
                const likeCard = createStatCard("Toplam Beğeni", stats.totalLikes);
                const commentCard = createStatCard("Toplam Yorum", stats.totalComments);
                const viewCard = createStatCard("Toplam Görüntülenme", stats.totalViews);
            
                statRow.appendChild(totalContentCard);
                statRow.appendChild(restrictedCard);
                statRow.appendChild(unrestrictedCard);
                statRow.appendChild(likeCard);
                statRow.appendChild(commentCard);
                statRow.appendChild(viewCard);
            
                statisticsContainer.appendChild(statRow);
            
                // En çok etkileşim alan içerik - En üste
                if (stats.mostInteractedContent) {
                    const content = stats.mostInteractedContent;
            
                    const mostInteractedCard = document.createElement("div");
                    mostInteractedCard.classList.add("stat-card", "highlight-card");
            
                    mostInteractedCard.innerHTML = `
                    <div class="most">
                        <div class="stat-header">
                            <span class="stat-description">En çok etkileşim alan içeriğin:</span>
                            <h3 class="content-title">
                                <a href="content-detail.html?postId=${content.contentId}">
                                    "${content.contentTitle}"
                                </a>
                            </h3>
                        </div>
                        <div class="stat-text">
                            <p><strong>Beğeni Sayısı:</strong> ${content.likeCount}</p>
                            <p><strong>Yorum Sayısı:</strong> ${content.commentCount}</p>
                            <p><strong>Görüntülenme Sayısı:</strong> ${content.viewCount}</p>
                            <p><strong>Toplam Etkileşim:</strong> ${content.totalInteraction}</p>
                        </div>
                    </div>
                    `;
            
                    statisticsContainer.appendChild(mostInteractedCard);
                }
            
                
            }

            // Tek kart oluşturucu yardımcı fonksiyon
            function createStatCard(title, value) {
                const card = document.createElement("div");
                card.classList.add("stat-card");

                card.innerHTML = `
                    <div class="stat-header">
                        <h3 class="stat-title">${title}</h3>
                    </div>
                    <div class="stat-value">${value}</div>
                `;

                return card;
            }

            // İstatistikleri al
            fetchUserStatistics();
        }

    } catch (error) {
        console.error("Kullanıcı bilgileri alınırken hata oluştu:", error);
    }
});
