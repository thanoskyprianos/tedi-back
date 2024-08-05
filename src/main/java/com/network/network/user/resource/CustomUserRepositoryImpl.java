package com.network.network.user.resource;

import com.network.network.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> findByNameLike(String name) {
        return em.createQuery("select u from User u where concat(u.firstName,' ', u.lastName) like :name", User.class)
                .setParameter("name", '%' + name + '%')
                .getResultList();
    }
}
