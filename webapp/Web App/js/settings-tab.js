document.addEventListener("DOMContentLoaded", function () {
  const buttons = document.querySelectorAll(".tab-button, .sub-tab-button");
  const contents = document.querySelectorAll(".tab-content");

  // İlk başta "Hesap Bilgileri" sekmesini aktif yap
  const defaultTabButton = document.querySelector(".sub-tab-button[data-subtab='account-info']");
  const defaultTabContent = document.getElementById("account-info");

  if (defaultTabButton && defaultTabContent) {
      defaultTabButton.classList.add("active");
      defaultTabContent.classList.add("active");
  }

  buttons.forEach((button) => {
      button.addEventListener("click", () => {
          // Aktif butonu güncelle
          buttons.forEach((btn) => btn.classList.remove("active"));
          button.classList.add("active");

          // İçerikleri güncelle
          const tabId = button.getAttribute("data-subtab") || button.getAttribute("data-tab");
          contents.forEach((content) => {
              content.classList.toggle("active", content.id === tabId);
          });
      });
  });
});
