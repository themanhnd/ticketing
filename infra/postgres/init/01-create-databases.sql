-- Chạy MỘT LẦN khi PostgreSQL khởi tạo lần đầu (volume dữ liệu còn trống).
-- Mỗi service: một user riêng + một database riêng do user đó sở hữu.

CREATE USER event_svc WITH PASSWORD 'event_svc';
CREATE DATABASE event_db OWNER event_svc;
REVOKE CONNECT ON DATABASE event_db FROM PUBLIC;

CREATE USER inventory_svc WITH PASSWORD 'inventory_svc';
CREATE DATABASE inventory_db OWNER inventory_svc;
REVOKE CONNECT ON DATABASE inventory_db FROM PUBLIC;

CREATE USER order_svc WITH PASSWORD 'order_svc';
CREATE DATABASE order_db OWNER order_svc;
REVOKE CONNECT ON DATABASE order_db FROM PUBLIC;