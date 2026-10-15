-- Dữ liệu mẫu cho môi trường dev.
-- File này chạy lại mỗi khi nội dung thay đổi, nên mọi câu lệnh phải chạy nhiều lần mà không sinh trùng.

-- ===== Sân =====
INSERT INTO venues (name, address, capacity)
SELECT 'Sân vận động Mỹ Đình', 'Lê Đức Thọ, Nam Từ Liêm, Hà Nội', 40000
    WHERE NOT EXISTS (SELECT 1 FROM venues WHERE name = 'Sân vận động Mỹ Đình');

INSERT INTO venues (name, address, capacity)
SELECT 'Nhà hát Lớn Hà Nội', '1 Tràng Tiền, Hoàn Kiếm, Hà Nội', 600
    WHERE NOT EXISTS (SELECT 1 FROM venues WHERE name = 'Nhà hát Lớn Hà Nội');

-- ===== Sự kiện =====
-- Trận đấu đang mở bán
INSERT INTO events (venue_id, name, type, starts_at, status)
SELECT v.id, 'Việt Nam vs Thái Lan', 'MATCH', TIMESTAMPTZ '2026-12-20 19:00:00+07', 'ON_SALE'
FROM venues v
WHERE v.name = 'Sân vận động Mỹ Đình'
  AND NOT EXISTS (SELECT 1 FROM events WHERE name = 'Việt Nam vs Thái Lan');

-- Đêm nhạc còn ở trạng thái nháp
INSERT INTO events (venue_id, name, type, starts_at, status)
SELECT v.id, 'Đêm nhạc mùa đông', 'CONCERT', TIMESTAMPTZ '2027-01-10 20:00:00+07', 'DRAFT'
FROM venues v
WHERE v.name = 'Nhà hát Lớn Hà Nội'
  AND NOT EXISTS (SELECT 1 FROM events WHERE name = 'Đêm nhạc mùa đông');

-- ===== Khu khán đài =====
-- ON CONFLICT dựa vào ràng buộc UNIQUE (event_id, name) đã có từ V1
INSERT INTO sections (event_id, name, seating_type, capacity, price)
SELECT e.id, s.name, s.seating_type, s.capacity, s.price
FROM events e
         CROSS JOIN (VALUES
                         ('Khán đài A', 'NUMBERED',  5000, 500000),
                         ('Khán đài B', 'GENERAL',   8000, 300000),
                         ('Khán đài C', 'GENERAL',  10000, 200000)
) AS s(name, seating_type, capacity, price)
WHERE e.name = 'Việt Nam vs Thái Lan'
    ON CONFLICT (event_id, name) DO NOTHING;

INSERT INTO sections (event_id, name, seating_type, capacity, price)
SELECT e.id, s.name, s.seating_type, s.capacity, s.price
FROM events e
         CROSS JOIN (VALUES
                         ('Hạng VIP',     'NUMBERED', 100, 2000000),
                         ('Hạng thường',  'NUMBERED', 400,  800000)
) AS s(name, seating_type, capacity, price)
WHERE e.name = 'Đêm nhạc mùa đông'
    ON CONFLICT (event_id, name) DO NOTHING;