# Web Uygulaması - Spring Boot & PostgreSQL

Bu web uygulaması, **Spring Boot** ve **PostgreSQL** kullanılarak geliştirilmiştir. Kullanıcıların kayıt olabileceği, giriş yapabileceği ve içeriklerle etkileşime geçebileceği bir platform sunar. Proje, farklı kullanıcı rollerine sahip olup her bir rol için farklı erişim ve yetkilendirme seviyeleri sağlar.

## Kullanıcı Rolleri
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
- **Java  22.0.2**
- **Spring Boot  3.3.7**
- **Maven 3.9.9**
- **Spring Data JPA** 
- **Spring Security 3.4.2**
- **JWT (JSON Web Token) 0.11.5**
- **PostgreSQL**

### Frontend
- **HTML5, CSS3, JavaScript**: Temel web arayüzü ve etkileşimler için kullanılır.
  
## Bağımlılıklar

Bu proje **Spring Boot** tabanlı bir web uygulamasıdır ve yapılandırma yönetimi için **Maven** kullanılmıştır. 

- **Spring Boot Starter Web**  
  Web uygulamaları geliştirmek için gerekli tüm bileşenleri içerir (Tomcat, Jackson, vb.).

- **Spring Boot Starter Data JPA**  
  Veritabanı işlemleri için JPA desteği sağlar.

- **Spring Boot Starter Validation**  
  Veri doğrulama işlemleri için kullanılır (`@Valid`, `@NotNull`, vb.).

- **Spring Boot Starter Security** (`v3.4.2`)  
  Uygulama güvenliğini sağlamak için Spring Security bileşenlerini içerir.

- **PostgreSQL JDBC Driver**  
  PostgreSQL veritabanı ile bağlantı kurmak için kullanılır.

- **Lombok**  
  Boilerplate kodları azaltmak için (getter, setter, constructor, vb.) kullanılır.

- **JSON Web Token (JWT)**  
  Kullanıcı kimlik doğrulaması için aşağıdaki bağımlılıklar kullanılmıştır:
  - `jjwt-api`
  - `jjwt-impl`
  - `jjwt-jackson`

- **Spring Boot Starter Test**  
  Birim ve entegrasyon testleri için gerekli bağımlılıkları içerir.

##  Veritabanı - **PostgreSQL**

### Veritabanı Yapılandırması

Aşağıda `application.properties` dosyasında kullanılan temel yapılandırmalar yer almaktadır:
 - spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
 - spring.jpa.properties.hibernate.default_schema=web
 - spring.datasource.username=postgres
 - spring.datasource.password= password

### ER Diyagramı 
![ER Diyagramı](webapp/images/erdiagram.png)

### Tablo Yapısı
#### user:
![](webapp/images/user.png)
#### content:
![](webapp/images/content.png)
#### comment:
![](webapp/images/comment.png)
#### like:
![](webapp/images/like.png)


# Arayüz Görselleri
## Misafir Kullanıcı
### Anasayfa
![](webapp/images/anasayfa1.png)

![](webapp/images/anasayfa1-mobile.png)

![](webapp/images/anasayfa2.png)

![](webapp/images/anasayfa2-mobile.png)

![](webapp/images/anasayfa3.png)

![](webapp/images/anasayfa3-mobile.png)

![](webapp/images/anasayfa4.png)

![](webapp/images/anasayfa4-mobile.png)

![](webapp/images/anasayfa4-mobile2.png)

![](webapp/images/anasayfa5.png)

![](webapp/images/anasayfa5_1.png)

![](webapp/images/anasayfa5-mobile.png)

![](webapp/images/anasayfa5-mobile2.png)

### Giriş

![](webapp/images/giris1.png)

![](webapp/images/giris2.png)

![](webapp/images/giris1-mobile.png)



### Kayıt

![](webapp/images/kayit1.png)

![](webapp/images/kayit1-mobile.png)



### Kategoriler

![](webapp/images/kategori1.png)

![](webapp/images/kategori1-mobile.png)

### İçerik Detay

![](webapp/images/icerikdetay1.png)

![](webapp/images/icerikdetay1-mobile.png)

![](webapp/images/icerikdetay2.png)

![](webapp/images/icerikdetay2-mobile.png)

![](webapp/images/icerikdetay3.png)

### Son Eklenenler

![](webapp/images/soneklenenler1.png)

![](webapp/images/soneklenenler1-mobile.png)

### Popüler İçerikler

![](webapp/images/populer1.png)

![](webapp/images/populer1-mobile.png)



## Giriş Yapmış Kullanıcı
### Anasayfa

![](webapp/images/login-anasayfa.png)

![](webapp/images/login-anasayfa-mobile.png)

![](webapp/images/login-anasayfa-mobile1.png)

![](webapp/images/login-anasayfa-mobile2.png)

### Profil

![](webapp/images/login-profil-icerik.png)

![](webapp/images/ogin-profil-icerik-mobile.png)


![](webapp/images/login-profil-hareket-begeni.png)

![](webapp/images/ogin-profil-hareket-begeni-mobile.png)

![](webapp/images/login-profil-hareket-yorum.png)

![](webapp/images/ogin-profil-hareket-yorum-mobile.png)



![](webapp/images/login-profil-istatistik.png)

![](webapp/images/ogin-profil-icerik-istatistik.png)


![](webapp/images/login-profili-guncelle.png)

![](webapp/images/login-profiliguncelle-mobile.png)


### Ayarlar

![](webapp/images/login-ayarlar-hesap.png)

![](webapp/images/login-ayarlar-hesap-mobile.png)

![](webapp/images/login-ayarlar-sifre.png)

![](webapp/images/login-ayarlar-sifre-mobile.png)


![](webapp/images/login-ayarlar-sil1.png)

![](webapp/images/login-ayarlar-sil1-mobile.png)

![](webapp/images/login-ayarlar-sil2.png)

![](webapp/images/login-ayarlar-sil2-mobile.png)

![](webapp/images/login-ayarlar-sil3.png)

![](webapp/images/login-ayarlar-sil3-mobile.png)



![](webapp/images/login-ayarlar-oturum1.png)

![](webapp/images/login-ayarlar-oturum1-mobile.png)

![](webapp/images/login-ayarlar-oturum2.png)

![](webapp/images/login-ayarlar-oturum2-mobile.png)


### İçerik Detay

![](webapp/images/login-icerikdetay1.png)

![](webapp/images/login-icerikdetay1-mobile.png)

![](webapp/images/login-icerikdetay2.png)

![](webapp/images/login-icerikdetay3.png)

![](webapp/images/login-icerikdetay3-mobile.png)

![](webapp/images/login-icerikdetay4.png)


### Toast Message

![](webapp/images/toast-true.png)

![](webapp/images/toast-false.png)




























