document.addEventListener("DOMContentLoaded", function () {
    const uploadButton = document.getElementById("uploadButton");

    uploadButton.addEventListener("click", async function () {
        const formData = new FormData();
        formData.append("title", document.getElementById("title").value.trim());
        formData.append("text", document.getElementById("content").value.trim());
        formData.append("category", document.getElementById("category").value);

        // Kısıt durumu için true veya false değeri ekleyelim
        const restrictionValue = document.getElementById("restriction").value === "restricted";
        formData.append("isRestricted", restrictionValue);

        const imageInput = document.getElementById("image").files[0];
        if (imageInput) {
            formData.append("image", imageInput);
        }

        try {
            const response = await fetch("https://localhost:8443/contents/create", {
                method: "POST",
                credentials: "include", // Çerezi otomatik olarak gönder
                body: formData
            });

            const data = await response.json();

            if (response.ok) {
                alert("İçerik başarıyla yüklendi!");
                window.location.href = "/"; // Gerekirse yönlendirme
            } else {
                alert("Hata: " + data.message);
            }
        } catch (error) {
            console.error("İçerik yüklenirken hata oluştu:", error);
            alert("İçerik yüklenirken bir hata oluştu.");
        }
    });
});
