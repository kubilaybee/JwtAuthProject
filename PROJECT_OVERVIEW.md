# JWTAuth — Proje Dokümantasyonu

> Spring Boot 3.4 tabanlı, JWT kimlik doğrulama ve kullanıcı yönetim servisi.

---

## İçindekiler

- [Teknoloji Yığını](#teknoloji-yığını)
- [Mimari](#mimari)
- [Veri Modeli (Entity Diyagramı)](#veri-modeli)
- [Kimlik Doğrulama Akışı](#kimlik-doğrulama-akışı)
- [API Endpoint'leri](#api-endpointleri)
- [Güvenlik Katmanı](#güvenlik-katmanı)
- [Zamanlayıcı](#zamanlayıcı)

---

## Teknoloji Yığını

```mermaid
mindmap
  root((JWTAuth))
    Backend
      Java 17
      Spring Boot 3.4
      Spring Security 6
      Spring Data JPA
    Veritabanı
      PostgreSQL
      Flyway Migrations
      ShedLock
    Kimlik Doğrulama
      JJWT 0.12.5
      BCrypt
    E-posta
      JavaMail
      Thymeleaf Templates
    API
      SpringDoc OpenAPI
      Swagger UI
    Build & Dev
      Maven
      Docker Compose
      MailDev
      Lombok
      ModelMapper
```

| Katman | Teknoloji | Versiyon |
|---|---|---|
| Framework | Spring Boot | 3.4.0 |
| Dil | Java | 17 |
| Güvenlik | Spring Security + JJWT | 6.x / 0.12.5 |
| ORM | Spring Data JPA / Hibernate | — |
| Veritabanı | PostgreSQL | — |
| Migration | Flyway | — |
| E-posta | JavaMail + Thymeleaf | — |
| API Dokümantasyon | SpringDoc OpenAPI | 2.8.4 |
| Zamanlama | Spring Scheduler + ShedLock | 5.10.0 |
| Build | Maven | — |
| Geliştirme Servisleri | Docker Compose (PostgreSQL + MailDev) | — |

---

## Mimari

### Katmanlı Mimari

```mermaid
graph TD
    Client(["🌐 Client (Browser / Mobile / API)"])

    subgraph Spring Boot Application
        direction TB
        Filter["🔒 JwtFilter\n(Her istekte JWT doğrular)"]
        Controller["🎮 Controller Katmanı\nAuthenticationController\nMemberController"]
        Service["⚙️ Service Katmanı\nAuthenticationService · UserService\nJwtService · TokenService\nEmailService · RoleService"]
        Repository["🗄️ Repository Katmanı\nUserRepository · RoleRepository\nTokenRepository"]
        Schedule["⏱️ Scheduler\nTokenExpirationSchedule\n(her 5 dakika)"]
    end

    DB[("🐘 PostgreSQL")]
    Mail["📧 MailDev / SMTP"]

    Client -->|"HTTP Request\n(Bearer Token)"| Filter
    Filter --> Controller
    Controller --> Service
    Service --> Repository
    Repository --> DB
    Service -->|"Async e-posta"| Mail
    Schedule -->|"Süresi dolmuş token temizle"| Repository
```

### Paket Yapısı

```
com.auth.JWTAuth/
├── config/
│   ├── security/
│   │   ├── SecurityConfig.java       ← HTTP güvenlik kuralları, CORS, CSRF
│   │   └── JwtFilter.java            ← Her istekte JWT doğrulama filtresi
│   ├── ApplicationConfig.java        ← AuthProvider, PasswordEncoder (BCrypt)
│   ├── OpenApiConfig.java            ← Swagger konfigürasyonu
│   ├── MessageSourceConfig.java      ← i18n mesaj kaynağı
│   └── ScheduleConfig.java           ← ShedLock konfigürasyonu
├── controller/
│   ├── BaseController.java           ← Ortak auth context
│   ├── AuthenticationController.java ← Kayıt, giriş, onay, şifre sıfırlama
│   └── MemberController.java         ← Kullanıcı yönetimi
├── domain/
│   ├── entity/
│   │   ├── User.java                 ← UserDetails implement eder
│   │   ├── Role.java
│   │   └── Token.java
│   └── dto/
│       ├── BasicUserDTO.java
│       ├── RoleDTO.java
│       └── request/                  ← İstek DTO'ları
├── service/
│   ├── AuthenticationService.java
│   ├── UserService.java
│   ├── JwtService.java
│   ├── TokenService.java
│   ├── EmailService.java
│   └── RoleService.java
├── repository/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── TokenRepository.java
│   └── specification/
│       └── UserSpecification.java    ← Dinamik sorgular
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── types/                        ← BadRequest, NotFound, Permission, Conflict
├── schedule/
│   └── TokenExpirationSchedule.java
└── util/
    ├── ContextUtil.java              ← SecurityContext'ten mevcut kullanıcı
    ├── ModelMapperUtils.java
    └── validation/                   ← @ValidEmail, @ValidFirstName, @ValidLastName
```

---

## Veri Modeli

### Entity İlişki Diyagramı

```mermaid
erDiagram
    BASIC_USER {
        UUID id PK
        VARCHAR firstName
        VARCHAR lastName
        VARCHAR email UK
        VARCHAR password
        VARCHAR userStatus
        BOOLEAN isDeleted
        UUID createdBy
        TIMESTAMP createdAt
        UUID updatedBy
        TIMESTAMP updatedAt
    }

    BASIC_ROLE {
        UUID id PK
        VARCHAR name
    }

    BASIC_TOKEN {
        UUID id PK
        VARCHAR token UK
        TIMESTAMP expirationDate
        TIMESTAMP validatedAt
        BOOLEAN isDeleted
        UUID user_id FK
    }

    BASIC_USERS_ROLES {
        UUID user_id FK
        UUID role_id FK
    }

    BASIC_USER ||--o{ BASIC_USERS_ROLES : "sahip olur"
    BASIC_ROLE ||--o{ BASIC_USERS_ROLES : "atanır"
    BASIC_USER ||--o| BASIC_TOKEN : "sahip olur"
```

### Kullanıcı Durumları

```mermaid
stateDiagram-v2
    [*] --> PENDING : Kayıt olundu
    PENDING --> ACTIVE : Token onaylandı\n+ şifre set edildi
    PENDING --> EXPIRED : Token 10 dk içinde\nonaylanmadı
    ACTIVE --> PENDING : Şifre sıfırlama\ntalep edildi
    ACTIVE --> DEACTIVATED : Admin tarafından\ndeaktive edildi
    EXPIRED --> PENDING : Yeni token\ngönderildi
    DEACTIVATED --> [*]
```

---

## Kimlik Doğrulama Akışı

### Kayıt ve Aktivasyon

```mermaid
sequenceDiagram
    actor Kullanıcı
    participant API as REST API
    participant AuthService
    participant TokenService
    participant EmailService
    participant DB as PostgreSQL

    Kullanıcı->>API: GET /auth/register\n{firstName, lastName, email, role}
    API->>AuthService: register()
    AuthService->>DB: E-posta benzersiz mi?
    DB-->>AuthService: ✓ Benzersiz
    AuthService->>DB: Kullanıcı oluştur (PENDING)
    AuthService->>TokenService: 6 haneli token üret (10 dk)
    TokenService->>DB: Token kaydet
    AuthService->>EmailService: Aktivasyon e-postası gönder (async)
    EmailService-->>Kullanıcı: 📧 Aktivasyon kodu e-postası
    API-->>Kullanıcı: 200 OK

    Kullanıcı->>API: GET /auth/confirm\n{token, password}
    API->>AuthService: confirmAccount()
    AuthService->>DB: Token geçerli mi? Süresi dolmadı mı?
    DB-->>AuthService: ✓ Geçerli
    AuthService->>DB: Kullanıcıyı ACTIVE yap\nŞifreyi BCrypt ile şifrele
    AuthService->>DB: Token'ı validated olarak işaretle
    API-->>Kullanıcı: 200 OK "Hesap aktifleştirildi"
```

### Giriş ve JWT Kullanımı

```mermaid
sequenceDiagram
    actor Kullanıcı
    participant API as REST API
    participant AuthService
    participant JwtService
    participant JwtFilter
    participant DB as PostgreSQL

    Kullanıcı->>API: GET /auth/login\n{email, password}
    API->>AuthService: authenticate()
    AuthService->>DB: Kullanıcıyı getir
    DB-->>AuthService: User (ACTIVE)
    AuthService->>AuthService: BCrypt şifre doğrulama
    AuthService->>JwtService: JWT üret
    JwtService-->>AuthService: Bearer token\n(~100 gün geçerli)
    API-->>Kullanıcı: 200 OK {token: "eyJ..."}

    Note over Kullanıcı, DB: Sonraki istekler

    Kullanıcı->>API: GET /users/1/10\nAuthorization: Bearer eyJ...
    API->>JwtFilter: doFilterInternal()
    JwtFilter->>JwtService: Token geçerli mi?
    JwtService-->>JwtFilter: ✓ Geçerli, email: user@x.com
    JwtFilter->>DB: Kullanıcıyı yükle
    JwtFilter->>JwtFilter: SecurityContext'e set et
    API-->>Kullanıcı: 200 OK [kullanıcı listesi]
```

### Şifre Sıfırlama

```mermaid
sequenceDiagram
    actor Kullanıcı
    participant API as REST API
    participant AuthService
    participant TokenService
    participant EmailService
    participant DB as PostgreSQL

    Kullanıcı->>API: GET /auth/forgot-password\n{email}
    API->>AuthService: forgotPassword()
    AuthService->>DB: Kullanıcıyı PENDING yap\nŞifreyi null yap
    AuthService->>TokenService: Yeni 6 haneli token üret
    TokenService->>DB: Token kaydet (10 dk)
    AuthService->>EmailService: Şifre sıfırlama e-postası (async)
    EmailService-->>Kullanıcı: 📧 Sıfırlama kodu
    API-->>Kullanıcı: 200 OK

    Kullanıcı->>API: GET /auth/confirm\n{token, newPassword}
    API-->>Kullanıcı: 200 OK "Şifre güncellendi"
```

### Token Temizleme (Scheduler)

```mermaid
flowchart LR
    Timer(["⏱️ Her 5 dakika\n(Cron: 0 0/5 * * * *)"])
    ShedLock["🔒 ShedLock\nTek instance'ın\nçalışmasını garantiler"]
    Query["Süresi dolmuş\nve onaylanmamış\ntokenleri bul"]
    Update1["Token → isDeleted=true"]
    Update2["Kullanıcı → status=EXPIRED"]

    Timer --> ShedLock --> Query --> Update1 --> Update2
```

---

## API Endpoint'leri

**Base URL:** `http://localhost:8080/api/v1/`

### Kimlik Doğrulama (`/auth`) — Herkese Açık

| Yöntem | Endpoint | Açıklama | İstek Gövdesi |
|---|---|---|---|
| GET | `/auth/register` | Yeni kullanıcı kaydı | `{firstName, lastName, email, role}` |
| GET | `/auth/confirm` | Hesap aktivasyonu | `{token, password}` |
| GET | `/auth/login` | Giriş yap → JWT al | `{email, password}` |
| GET | `/auth/forgot-password` | Şifre sıfırlama talebi | `{email}` |

### Kullanıcı Yönetimi (`/users`) — Kimlik Doğrulama Gerektirir

| Yöntem | Endpoint | Açıklama | Yetki |
|---|---|---|---|
| GET | `/users/{page}/{size}` | Sayfalı kullanıcı listesi (opsiyonel: firstName, lastName, email filtresi) | Herhangi bir auth |
| GET | `/users/{uuid}` | UUID ile kullanıcı getir | Herhangi bir auth |
| PUT | `/users` | Kendi profilini düzenle | Herhangi bir auth (yalnızca kendi kaydı) |
| PUT | `/users/change-role` | Kullanıcı rolü değiştir | **Yalnızca SUPER** |

### Hata Yanıt Formatı

```json
{
  "status": 400,
  "errors": [
    {
      "source": "email",
      "detail": "Geçersiz e-posta formatı"
    }
  ]
}
```

---

## Güvenlik Katmanı

```mermaid
flowchart TD
    Request(["HTTP İsteği"])
    Public{"/auth/** veya\nSwagger?"}
    HasToken{"Authorization\nheader var mı?"}
    ValidToken{"JWT geçerli mi?"}
    LoadUser["Kullanıcıyı DB'den yükle"]
    SetContext["SecurityContext'e set et"]
    CheckRole{"Endpoint rolü\nuyuyor mu?"}
    Allow["✅ İsteği işle"]
    Deny401["❌ 401 Unauthorized"]
    Deny403["❌ 403 Forbidden"]

    Request --> Public
    Public -->|Evet| Allow
    Public -->|Hayır| HasToken
    HasToken -->|Hayır| Deny401
    HasToken -->|Evet| ValidToken
    ValidToken -->|Hayır| Deny401
    ValidToken -->|Evet| LoadUser
    LoadUser --> SetContext
    SetContext --> CheckRole
    CheckRole -->|Uyuyor| Allow
    CheckRole -->|Uymuyor| Deny403
```

### JWT Token İçeriği

| Alan | Değer |
|---|---|
| Subject | Kullanıcı e-postası |
| `fullName` claim | Ad Soyad |
| `authorities` claim | Kullanıcı rolleri |
| İmzalama | HMAC SHA-256 |
| Geçerlilik | 8.640.000 ms (~100 gün) |

### Rol Yetkilendirmesi

| Rol | Yetkiler |
|---|---|
| **SUPER** | Tüm endpoint'ler + rol atama |
| **ADMIN** | Kullanıcı listeleme, görüntüleme, profil düzenleme |
| **USER** | Kullanıcı listeleme, görüntüleme, kendi profilini düzenleme |

---

## Zamanlayıcı

```mermaid
gantt
    title Token Yaşam Döngüsü
    dateFormat mm:ss
    axisFormat %M:%S

    section Token
    Token oluşturuldu      : milestone, 00:00, 0m
    Geçerli süre (10 dk)   : active, tok1, 00:00, 10m
    Süresi doldu           : milestone, 10:00, 0m

    section Scheduler (her 5 dk)
    1. çalışma             : milestone, 05:00, 0m
    2. çalışma (temizlik)  : milestone, 10:00, 0m
```

- Cron ifadesi: `0 0/5 * * * *` (her 5 dakikada bir)
- ShedLock ile yalnızca tek instance çalışır
- Süresi dolmuş tokenlar `isDeleted=true` yapılır
- İlgili kullanıcı `EXPIRED` statüsüne alınır

---

## Geliştirme Ortamı Kurulumu

```bash
# 1. Servisleri başlat (PostgreSQL + MailDev)
docker-compose up -d

# 2. Uygulamayı çalıştır
mvn spring-boot:run

# 3. Swagger UI
http://localhost:8080/api/v1/swagger-ui.html

# 4. MailDev (e-posta önizleme)
http://localhost:1080

# 5. Testleri çalıştır
mvn test
```

---

## Veritabanı Migrasyonları (Flyway)

| Versiyon | Dosya | İçerik |
|---|---|---|
| V1.1 | `V1_1__initial_create.sql` | Tabloları oluşturur (basic_role, basic_user, basic_token, basic_users_roles) |
| V1.2 | `V1_2__add_foreign_keys.sql` | Foreign key kısıtlamaları |
| V1.3 | `V1_3__initial_insert.sql` | Varsayılan rolleri ekler (SUPER, ADMIN, USER) |
| V1.4 | `V1_4__create_super_user.java` | Super kullanıcıyı programatik olarak oluşturur |
