package com.network.network.role.resource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class TruncateRoleTableImpl implements TruncateRoleTable {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void truncateRoleTable() {
        em.createNativeQuery("SET foreign_key_checks = 0;").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE `role`;").executeUpdate();
        em.createNativeQuery("SET foreign_key_checks = 0;").executeUpdate();
    }
}
