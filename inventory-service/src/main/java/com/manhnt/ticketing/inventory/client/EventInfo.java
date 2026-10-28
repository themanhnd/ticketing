package com.manhnt.ticketing.inventory.client;

import java.util.List;

public record EventInfo(Long id, String name, String status, List<SectionInfo> sections) {
}