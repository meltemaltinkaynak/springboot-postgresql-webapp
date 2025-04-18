# Web Uygulaması - Spring Boot & PostgreSQL

Bu web uygulaması, **Spring Boot** ve **PostgreSQL** kullanılarak geliştirilmiştir. Kullanıcıların kayıt olabileceği, giriş yapabileceği ve içeriklerle etkileşime geçebileceği bir platform sunar. Proje, farklı kullanıcı rollerine sahip olup her bir rol için farklı erişim ve yetkilendirme seviyeleri sağlar.

## Kullanıcı Roller
- **User (Kullanıcı)**: Kayıt olma, giriş yapma, içeriklerle etkileşime geçme (beğenme, yorum yapma) ve kendi profilini ve aktivitelerini görüntüleme yetkisine sahiptir.
- **Content Admin (İçerik Yöneticisi)**: İçerik oluşturma, düzenleme ve silme işlemleri yapabilir.
- **Super Admin (Süper Admin)**: Kullanıcı yönetimi ve içerik görünürlüğü gibi ekstra yetkilere sahiptir.

## Ana Özellikler
- **Kullanıcı Kaydı ve Kimlik Doğrulama**: Kullanıcılar, JWT (JSON Web Token) ve çerezler aracılığıyla kayıt olabilir ve giriş yapabilir.
- **İçerik Erişim Kontrolü**:
  - **Misafir Kullanıcılar**: Sınırlı içeriklere erişebilir.
  - **Giriş Yapmış Kullanıcılar**: Tüm içeriklere erişebilir ve etkileşimde bulunabilir (beğenme, yorum yapma).
  - **Admin Rolleri**: İçerik Yöneticisi ve Süper Admin içerik paylaşabilir, düzenleyebilir ve silebilir.
- **Profil Yönetimi**: Kullanıcılar, profil bilgilerini güncelleyebilir ve kendi içeriklerini, aktivitelerini ve istatistiklerini görüntüleyebilir.
- **İçerik Kategorileri**: Kullanıcılar içerikleri kategorilere göre filtreleyebilir ve görüntüleyebilir.
- **Son Paylaşılan İçerikler**: Kullanıcılar en son paylaşılan içerikleri görüntüleyebilir.

## Kullanılan Teknolojiler
### Backend
- **Spring Boot**: Backend kısmı için RESTful API geliştirilmiştir.
- **Spring Data JPA**: Veritabanı ile etkileşim ve ORM için kullanılır.
- **Spring Security**: Kullanıcı doğrulama ve yetkilendirme işlemleri için kullanılır.
- **JWT (JSON Web Token)**: Güvenli kimlik doğrulama için token ve çerezler kullanılır.
- **PostgreSQL**: Kullanıcı ve içerik verilerinin saklandığı ilişkisel veritabanıdır.

### Frontend
- **HTML, CSS, JavaScript**: Temel web arayüzü ve etkileşimler için kullanılır.
  
## Veritabanı
- **PostgreSQL**
## Kullanıcı Arayüzü




