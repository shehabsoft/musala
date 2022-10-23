package com.musala.drone.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditMapperTest {

    private AuditMapper auditMapper;

    @BeforeEach
    public void setUp() {
        auditMapper = new AuditMapperImpl();
    }
}
