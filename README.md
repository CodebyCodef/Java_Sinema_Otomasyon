# Sinema Bilet Otomasyon

JavaFX tabanlı sinema bilet satış otomasyon sistemi.

## Proje Yapısı

```
Sinema Bilet Otomasyon/
├── src/
│   └── main/
│       ├── java/com/example/sinemabiletotomasyon/
│       │   ├── LoginApplication.java      # Uygulama giriş noktası
│       │   ├── LoginController.java        # Giriş ekranı kontrolü
│       │   ├── MenuController.java         # Ana menü kontrolü
│       │   ├── DashboardScreenController.java  # Dashboard kontrolü
│       │   ├── AdminScreenController.java  # Admin panel kontrolü
│       │   ├── BuyTicketScreenController.java  # Bilet satış kontrolü
│       │   ├── TicketScreenController.java # Bilet görüntüleme kontrolü
│       │   ├── DatabaseHelper.java         # Veritabanı bağlantısı
│       │   └── module-info.java            # Modül bilgileri
│       └── resources/com/example/sinemabiletotomasyon/
│           ├── LoginScreen.fxml            # Giriş ekranı UI
│           ├── MenuScreen.fxml              # Ana menü UI
│           ├── DashboardScreen.fxml         # Dashboard UI
│           ├── AdminScreen.fxml             # Admin panel UI
│           ├── BuyTicketScreen.fxml        # Bilet satış UI
│           └── TicketScreen.fxml           # Bilet görüntüleme UI
├── Filmler/                                 # Film afişleri
├── pom.xml                                  # Maven yapılandırması
└── mvnw                                     # Maven wrapper
```

## Özellikler

- Kullanıcı yönetimi (Admin/Müşteri)
- Film ve seans yönetimi
- Kolay rezervasyon ve bilet satışı
- SQL Server veritabanı entegrasyonu

## Teknolojiler

- Java 24
- JavaFX 17
- Maven
- SQL Server (mssql-jdbc)

## Kurulum

```bash
mvn clean javafx:run
```

## Gereksinimler

- Java JDK 17+
- Maven 3.8+
- SQL Server

## Veritabanı

Veritabanı yapısı için `sinauto.bak` dosyasını SQL Server'a restore edin.

## Ek Dosyalar

- `Filmler/` - Film afiş görselleri
- `LİBS/` - Harici kütüphaneler (JavaFX, SQL Server JDBC)
