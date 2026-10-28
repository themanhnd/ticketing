package com.manhnt.ticketing.inventory.client;

// Chỉ lấy những trường inventory cần. Giá vé (price) không liên quan đến ghế nên bỏ qua.
public record SectionInfo(Long id, String name, SeatingType seatingType, int capacity) {
}