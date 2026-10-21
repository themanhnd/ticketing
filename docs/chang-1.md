# Chặng 1: event-service

> Hoàn thành: 15/10/2026
> Kết quả: một service Spring Boot hoàn chỉnh với PostgreSQL, REST API, xử lý lỗi, tài liệu OpenAPI và 4 tầng test.

## 1. Đã xây được gì

- **event-service** (cổng 8081): quản lý sân (`Venue`), sự kiện (`Event`) và khu khán đài (`Section`).
- API: tạo sân, tạo sự kiện, thêm khu khán đài, mở bán, xem chi tiết, xem danh sách có phân trang.
- Stack: Java 25, Spring Boot 4.1, Maven, PostgreSQL 17, Flyway, Testcontainers, springdoc-openapi.

### Cấu trúc package (chia theo tính năng)

```text
com.manhnt.ticketing.event
├── EventServiceApplication.java
├── common/   exception dùng chung, GlobalExceptionHandler, OpenApiConfig
├── venue/    Venue, VenueRepository, VenueService, VenueController, DTO
└── event/    Event, Section, enum, EventRepository, EventService, EventController, DTO
```

### Cấu trúc resources

```text
resources/
├── db/migration/V1__init.sql        cấu trúc bảng, chạy ở mọi môi trường
├── db/dev-data/R__sample_data.sql   dữ liệu mẫu, chỉ dùng khi dev
└── application.yaml
```

## 2. Khái niệm chính

### Kiến trúc 3 lớp

```text
Controller  nhận/trả JSON, @Valid kiểm tra đầu vào, không chứa logic nghiệp vụ
   ↓ DTO
Service     mở transaction, điều phối, đổi entity sang DTO trước khi trả ra
   ↓
Entity      chứa luật nghiệp vụ (publish, addSection)
Repository  đọc ghi DB, Spring Data tự sinh code
```

### Spring Boot

- **Dependency injection qua constructor**: Spring tự truyền dependency, không cần `@Autowired`.
- **Docker Compose Support**: chạy app là Spring tự bật container từ `compose.yaml` và tự cấu hình kết nối DB.
- **Actuator**: `/actuator/health` để kiểm tra app còn sống.

### Database và Flyway

- Mỗi thay đổi cấu trúc DB là một file `V<số>__<mô_tả>.sql`, chạy đúng một lần, theo thứ tự.
- **Không bao giờ sửa file migration đã chạy** (Flyway báo `checksum mismatch`). Muốn đổi thì tạo `V2__...`.
- **Repeatable migration** `R__...` chạy sau các file versioned và chạy lại khi nội dung đổi. Phải viết idempotent (`WHERE NOT EXISTS`, `ON CONFLICT DO NOTHING`).
- `ddl-auto: validate`: Hibernate chỉ so entity với bảng thật, không tự tạo bảng.
- Ràng buộc trong DB (`CHECK`, `UNIQUE`, `NOT NULL`) là hàng rào cuối cùng, kể cả khi code Java có bug.
- Tiền dùng `NUMERIC` + `BigDecimal`, không bao giờ dùng `float`/`double`.
- Thời gian dùng `TIMESTAMPTZ` + `Instant`.

### JPA và Hibernate

- Entity **không có setter**. Muốn đổi trạng thái phải gọi method có kiểm tra luật (`publish()`, `addSection()`).
- `@Enumerated(EnumType.STRING)`: lưu chữ, không lưu số thứ tự.
- **Dirty checking**: entity lấy ra trong transaction được theo dõi; đổi field thì Hibernate tự `UPDATE` khi commit, không cần `save()`.
- **Persistence context** là bộ nhớ đệm của Hibernate. `flush()` ép ghi xuống DB, `clear()` xoá bộ đệm.
- Section mới thêm vào danh sách chỉ được `INSERT` khi flush, nên cần `flush()` nếu muốn có `id` ngay trong response.
- `open-in-view: false`: ra khỏi transaction thì không tải lazy được nữa. **Đổi sang DTO bên trong service.**
- **N+1**: danh sách dùng DTO gọn, không kéo theo quan hệ.

### REST API

- DTO dùng `record`, không bao giờ trả entity ra JSON.
- Mã HTTP: **201** tạo mới, **400** dữ liệu sai, **404** không tìm thấy, **409** vi phạm luật/xung đột, **500** lỗi server.
- Phân trang bằng `Pageable` + `PagedModel`.

### Xử lý lỗi tập trung

- Code nghiệp vụ ném exception mô tả **chuyện gì xảy ra** (`ResourceNotFoundException`, `BusinessRuleException`), không biết gì về HTTP.
- `@RestControllerAdvice` (`GlobalExceptionHandler`) là nơi duy nhất **dịch** exception sang HTTP.
- Spring chọn handler theo kiểu exception, **cụ thể nhất thắng**. `Exception.class` là lưới an toàn cuối.
- Kế thừa `ResponseEntityExceptionHandler` để có sẵn xử lý cho các lỗi chuẩn (JSON hỏng, sai enum...).
- Định dạng lỗi chuẩn: `ProblemDetail` (RFC 9457).
- Không bao giờ trả `ex.getMessage()` của lỗi lạ ra ngoài; chỉ ghi log.
- Vì sao không dùng `IllegalStateException`: thư viện cũng ném nó, sẽ biến bug thật thành "409".

### Test: tháp test

| Tầng | Công cụ | Kiểm tra | Tốc độ |
|---|---|---|---|
| Unit | JUnit + AssertJ | Luật nghiệp vụ trong entity | ~0,1 s |
| Service | Mockito (`@Mock`, `@InjectMocks`, `verify`) | Service điều phối đúng | ~0,8 s |
| Web | `@WebMvcTest` + `MockMvcTester` + `@MockitoBean` | JSON vào/ra, validation, mã HTTP, xử lý lỗi | ~2,5 s |
| Integration | `@SpringBootTest` + Testcontainers | Chạy với PostgreSQL thật | vài giây |

- Khuôn **Arrange – Act – Assert**. Tên test đọc như một câu mô tả luật.
- Test cả **giá trị biên** (`>` hay `>=`).
- Test là lưới an toàn khi sửa code.

## 3. Quyết định thiết kế và lý do

| Quyết định | Lý do |
|---|---|
| Chia package theo tính năng | Mọi thứ của một tính năng ở một chỗ; giấu được constructor (package-private); dễ tách thành service riêng |
| Luật nghiệp vụ nằm trong entity | Test nhanh không cần Spring; không ai lách được luật |
| Server chạy UTC | Dữ liệu thời gian nhất quán; giao diện tự đổi sang giờ địa phương |
| Ghim phiên bản image (`postgres:17`) | `latest` đổi theo thời gian, test và app phải chạy cùng phiên bản |
| Dữ liệu mẫu tách thư mục `dev-data` | Bật/tắt được; DB test luôn sạch |
| Giữ DB ở local, chưa dùng VPS | Test vẫn cần Docker local; các chặng sau cần Redis, Kafka; tránh rủi ro bảo mật |

## 4. Những "bẫy" đã gặp

| Triệu chứng | Nguyên nhân | Cách sửa |
|---|---|---|
| Git cảnh báo `LF will be replaced by CRLF` | Windows và Linux kết thúc dòng khác nhau | `.gitattributes` với `* text=auto eol=lf` |
| Import test bị tô đỏ, `@Test` không nhận | File test nằm trong `src/main/java` | Test phải ở `src/test/java`, cùng tên package |
| `No Docker Compose file found in directory 'F:\ticketing\.'` | IntelliJ chạy app từ thư mục gốc | Working directory = `$MODULE_WORKING_DIR$` (sửa ở template) |
| `invalid value for parameter "TimeZone": "Asia/Saigon"` | JVM gửi tên múi giờ cũ, PostgreSQL bản mới không nhận | Chạy JVM bằng UTC: `TimeZone.setDefault` trong `main` + `argLine` cho test |
| Test chạy `postgres:latest` | Testcontainers dùng image khác app | Ghim `postgres:17` |
| Lỗi 500 khi thêm khu, tưởng do bỏ `flush()` | Thực ra trận đã `ON_SALE`, luật nghiệp vụ chặn | Bài học: cần xử lý lỗi rõ ràng, và đọc log trước khi kết luận |
| `Password authentication is not supported` khi `git push` | Clone bằng HTTPS, GitHub không nhận mật khẩu | Dùng SSH key, `git remote set-url origin git@github.com:...` |
| `Permission denied` khi chạy `./mvnw` trên Linux | File tạo trên Windows không có quyền thực thi | `chmod +x mvnw` + `git update-index --chmod=+x mvnw` |
| `Could not find a valid Docker environment for Testcontainers` | User Linux chưa có quyền dùng Docker | `usermod -aG docker $USER` rồi **đăng nhập lại** |
| Cảnh báo `Dynamic loading of agents...` | Mockito tự gắn agent vào JVM | Chỉ là cảnh báo; xử lý ở Chặng 8 |

### Cách đọc log khi gặp lỗi

1. Bỏ qua `CONDITIONS EVALUATION REPORT` (không phải lỗi).
2. Tìm dòng `ERROR` hoặc dòng **`Caused by` cuối cùng**.
3. Đọc kỹ thông điệp, thường nó đã nói rõ nguyên nhân.
4. So với lần chạy thành công gần nhất: khác nhau ở đâu (thư mục, tham số, phiên bản)?

## 5. Lệnh hay dùng

```bash
./mvnw spring-boot:run                 # chạy app (Windows: .\mvnw)
./mvnw test -Dtest=EventTest           # chạy riêng một class test
./mvnw verify                          # biên dịch + chạy toàn bộ test + đóng gói
docker compose down -v                 # xoá container VÀ dữ liệu (giả lập máy mới)
```

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Health: `http://localhost:8081/actuator/health`

## 6. Tự kiểm tra

Trả lời được mà không cần xem lại code là đã nắm chắc:

1. Vì sao không trả entity thẳng ra JSON?
2. Vì sao `publish()` không cần gọi `save()`?
3. Bỏ `flush()` và `clear()` trong `EventRepositoryTest` thì test còn kiểm tra được gì?
4. Khi nào trả 404, khi nào 409?
5. Exception đi qua những tầng nào trước khi tới `GlobalExceptionHandler`?
6. Vì sao lỗi validation xảy ra trước khi method controller được gọi?
7. Versioned migration khác repeatable migration thế nào?
8. Unit test, `@WebMvcTest` và Testcontainers mỗi loại kiểm tra được điều gì mà loại kia không làm được?

## 7. Việc còn treo

- [ ] Tự viết test giá trị biên: tổng sức chứa đúng bằng sức chứa sân thì thành công
- [ ] Thí nghiệm làm hỏng `publish()` và ghi lại những test nào đỏ
- [ ] Kiểm tra `docker compose version` trên máy Linux; cài Docker Engine bản chính thức nếu thiếu Compose v2

## 8. Ghi chú của tôi

> Phần này tự viết bằng lời của mình: điều gì khó nhất, điều gì bất ngờ nhất, nếu làm lại sẽ làm khác ở đâu.
