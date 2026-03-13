# Expense Tracker Portfolio

Starter project สำหรับทำ portfolio แบบ full-stack ด้วย `Next.js + Spring Boot + PostgreSQL + Docker Compose`

## Stack

- `client/` : Next.js 16 + TypeScript
- `server/` : Spring Boot 3.5 + Spring Data JPA + PostgreSQL
- `db` : PostgreSQL 17

## Server Structure

ฝั่ง `server/` ใช้ `layered architecture` โดยแยกโค้ดตามหน้าที่ของแต่ละชั้น

- `config` : เก็บ class สำหรับตั้งค่าระบบ เช่น security, CORS, seed data
- `controller` : รับ HTTP request และคืน HTTP response
- `service` : จัดการ business logic ของระบบ
- `repository` : เข้าถึงข้อมูลใน database ผ่าน Spring Data JPA
- `entity` : model ที่ map กับตารางใน PostgreSQL
- `dto` : request/response model สำหรับรับส่งข้อมูลระหว่าง client กับ server
- `exception` : จัดการ custom exception และ global exception handler

โครงสร้างหลัก:

```text
server/src/main/java/com/portfolio/expensetracker
├── config
├── controller
├── dto
│   ├── category
│   ├── common
│   └── transaction
├── entity
├── exception
├── repository
└── service
```

## Request Flow

ตัวอย่าง flow ของ request ในระบบ:

1. `client` เรียก API มาที่ `controller`
2. `controller` ส่งงานให้ `service`
3. `service` เรียก `repository` เพื่ออ่านหรือเขียนข้อมูล
4. `repository` จัดการข้อมูลผ่าน `entity` และ PostgreSQL
5. `service` แปลงผลลัพธ์เป็น `dto response`
6. ถ้าเกิดข้อผิดพลาด ระบบจะส่งต่อไปที่ `exception handler`

สรุปแบบสั้น:

```text
Controller -> Service -> Repository -> Database
                     -> DTO Response
```

## Run with Docker

เปิด Docker Desktop หรือให้ Docker daemon ทำงานก่อน

รันจาก root project:

```bash
docker compose up --build
```

ถ้าต้องการรันแบบ background:

```bash
docker compose up --build -d
```

หลังจาก container พร้อมใช้งาน:

- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080/v1/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

ดูสถานะ service:

```bash
docker compose ps
```

ดู log:

```bash
docker compose logs -f
```

หยุดการทำงาน:

```bash
docker compose down
```

ถ้าต้องการลบ volume ของ PostgreSQL ด้วย:

```bash
docker compose down -v
```

หมายเหตุ:

- `db` ใช้ PostgreSQL ที่ port `5432`
- `server` ใช้ Spring Boot ที่ port `8080`
- `client` ใช้ Next.js ที่ port `3000`
- ข้อมูล category เริ่มต้นจะถูก seed อัตโนมัติเมื่อ database ยังไม่มีข้อมูล

## Useful APIs

- `GET /v1/api/categories`
- `POST /v1/api/categories`
- `POST /v1/api/auth/register`
- `GET /v1/api/transactions?month=2026-03`
- `POST /v1/api/transactions`
- `PUT /v1/api/transactions/{id}`
- `DELETE /v1/api/transactions/{id}`
- `GET /v1/api/reports/summary?month=2026-03`

## Local development

Backend:

```bash
cd server
./mvnw spring-boot:run
```

Frontend:

```bash
cd client
npm run dev
```
