// Sayfa geçiş fonksiyonu
function navigateTo(page) {
  // Tüm içerik bölümlerini gizle
  document.getElementById("kullaniciYonetimi").classList.add("hidden");
  document.getElementById("icerikYonetimi").classList.add("hidden");

  // İlgili sayfayı göster
  if (page === "kullaniciYonetimi") {
    document.getElementById("kullaniciYonetimi").classList.remove("hidden");
  } else if (page === "icerikYonetimi") {
    document.getElementById("icerikYonetimi").classList.remove("hidden");
  }
}
