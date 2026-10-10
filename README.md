# Estate Elite

**Web-based Property Sales System**  
SE2030 – Software Engineering | Year 2, Semester 1 – 2026  
Group **26-MTR-SE2030-04** | Sri Lanka Institute of Information Technology (SLIIT)

Estate Elite is a full-stack web platform for buying and selling residential properties in Sri Lanka. Sellers list properties, buyers search and reserve, agents manage viewings, finance officers handle invoices and payments, and admins maintain agencies and content moderation.

---

## Features

| Module | Capabilities |
|--------|----------------|
| **Authentication** | Role-based register & login (Buyer, Seller, Agent, Finance, Admin) |
| **Property listings** | Create / edit / delete listings with images, district, parking, pool |
| **Search & wishlist** | Filter by district, price, bedrooms; save and prioritise favourites |
| **Appointments** | Agent availability slots, book / confirm / decline / reschedule |
| **Payments** | Card and bank-transfer payments (Strategy pattern); reserve with deposit |
| **Finance** | Invoices, PDF export, verify bank receipts, revenue summary |
| **Agencies** | Agency directory, link agents to agencies and district branches |
| **Reviews & flags** | Property / agent ratings; flag inappropriate content with notes |
| **Notifications** | In-app alerts (e.g. new appointment for sellers) |
| **UI** | Dark / light theme toggle, blue accent, consistent mesh background |

---

## Tech stack

- **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security  
- **Frontend:** Thymeleaf, HTML5, CSS3 (custom properties), vanilla JavaScript  
- **Database:** MySQL  
- **Build:** Maven  
- **Other:** Multipart file upload (bank receipts, property images)

---

## Design patterns

| Pattern | Where used |
|---------|------------|
| **Strategy** | Payment methods (Card / Bank Transfer); search filters |
| **Builder** | Property construction; payment context |
| **Factory** | Agency creation |
| **Observer** | Notification publishing to users |
| **State** | Appointment lifecycle (pending → confirmed / declined / cancelled) |

---

## Project structure (high level)

```
estate-elite/
├── src/main/java/com/estateelite/
│   ├── controller/          # REST & page controllers
│   ├── service/             # Business logic
│   ├── repository/          # JPA repositories
│   ├── model/ / entity/     # Domain entities
│   ├── payment/             # PaymentStrategy, Card, BankTransfer, PaymentContext
│   └── ...
├── src/main/resources/
│   ├── templates/           # Thymeleaf HTML
│   ├── static/css/          # theme.css and styles
│   ├── static/js/           # api.js and dashboard scripts
│   └── application.properties
├── pom.xml
└── README.md
```

---

## Prerequisites

- JDK 17+  
- Maven 3.8+  
- MySQL 8.x  
- (Optional) IDE: IntelliJ IDEA / Eclipse / VS Code  

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/<your-org>/estate-elite.git
cd estate-elite
```

### 2. Create the database

```sql
CREATE DATABASE estate_elite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

If you already have data, apply any column-length fixes only when needed (example):

```sql
USE estate_elite;
-- Only if you hit "Data truncated for column 'type'"
ALTER TABLE notifications MODIFY COLUMN type VARCHAR(64) NOT NULL;
```

### 3. Configure application properties

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/estate_elite?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# File upload limits (images & bank receipts)
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 4. Run the application

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package -DskipTests
java -jar target/estate-elite-*.jar
```

### 5. Open in browser

```
http://localhost:8080
```

---

## Default roles (after registration)

Register through the UI and select a role, or seed users as required for demos:

| Role | Typical use |
|------|-------------|
| **BUYER** | Search, wishlist, book viewings, reserve & pay |
| **SELLER** | List and manage properties |
| **AGENT** | Availability slots and appointments |
| **FINANCE** | Invoices, payments, bank-transfer verification |
| **ADMIN** | Agencies, agents, flagged reviews / agents |

---

## Payment flow (brief)

1. Buyer reserves a property or finance creates an invoice.  
2. Buyer chooses **Card** or **Bank Transfer**.  
3. Strategy pattern selects the correct handler (`CardPaymentStrategy` / `BankTransferPaymentStrategy`).  
4. Card → status **COMPLETED**; Bank transfer → **PENDING** until finance verifies the receipt.  
5. Property status updates (e.g. **RESERVED** / **SOLD**) according to business rules.  
6. Finance dashboard shows appointments, reservations, invoices, and total completed revenue.

---

## Team

| IT Number   | Name                 | Major function                          | Scrum role        |
|-------------|----------------------|-----------------------------------------|-------------------|
| IT25100170  | Thanthrige P.D.I.    | Agency Management                       | Dev / QA          |
| IT25101160  | Rukmalgoda R.D.D.    | Reviews, Ratings & Notifications        | Dev / QA          |
| IT25101986  | Thashmina P.G.D.     | Property Seller Module                  | Scrum Master      |
| IT25102047  | Palihakkara P.J.     | Payment & Transaction Module            | Product Owner     |
| IT25102054  | Dinali L.D.G.N.      | Agent Viewing & Appointment Module      | Development Team  |
| IT25102115  | Malka H.A.G.         | Buyer Search & Wishlist Module          | Development Team  |

---

## Agile delivery

Four Scrum sprints (12 weeks):

1. **Sprint 1** – Platform foundation & core creation (auth, listing, search, slots, agency, deposit)  
2. **Sprint 2** – Core interactions (edit listing, wishlist, confirm appointments, invoices, reviews)  
3. **Sprint 3** – Business rules & validation (access control, double-booking, refunds, notifications)  
4. **Sprint 4** – Polish, moderation & nice-to-haves (reschedule, comparison, flags, inbox)

---

## Screenshots

> Add screenshots under `/docs/screenshots/` and link them here after push.

| Screen | Description |
|--------|-------------|
| Home | Hero, CTAs, feature highlights |
| Browse | District / price / bedroom filters and property cards |
| Property detail | Gallery, attributes, wishlist / book / reserve |
| Buyer dashboard | Wishlist, viewings, transactions |
| Seller dashboard | Listings and stats |
| Agent dashboard | Availability slots and appointment requests |
| Finance dashboard | Invoices, bank transfers, revenue KPIs |

---

## API / form notes

- JSON APIs use `Api.post()` / `Api.get()` helpers in `static/js`.  
- Bank-transfer flows use **multipart** form posts (`Api.postForm`) for receipt upload.  
- Theme preference is stored in `localStorage` and applied via `data-theme` on `<html>` so it stays consistent across pages.

---

## License

Academic project for **SE2030 – Software Engineering**, SLIIT.  
Not licensed for commercial use unless the group decides otherwise.

---

## Acknowledgments

- SLIIT Faculty of Computing – SE2030 module  
- Lab sheets: Agile Development, Design Patterns  
- Group 26-MTR-SE2030-04

---

**Estate Elite** — premium property sales, built with Spring Boot.
