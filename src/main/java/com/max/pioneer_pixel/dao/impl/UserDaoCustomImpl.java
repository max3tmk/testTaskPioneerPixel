package com.max.pioneer_pixel.dao.impl;

import com.max.pioneer_pixel.dao.UserDaoCustom;
import com.max.pioneer_pixel.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoCustomImpl implements UserDaoCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<User> searchUsers(String namePrefix, String email, String phone, LocalDate dateOfBirthAfter, Pageable pageable) {
        StringBuilder baseQuery = new StringBuilder("SELECT u FROM User u");
        StringBuilder countQuery = new StringBuilder("SELECT COUNT(u) FROM User u");

        List<String> conditions = new ArrayList<>();
        if (namePrefix != null && !namePrefix.isEmpty()) {
            conditions.add("u.name LIKE :namePrefix");
        }
        if (email != null && !email.isEmpty()) {
            conditions.add("EXISTS (SELECT e FROM EmailData e WHERE e.user.id = u.id AND e.email = :email)");
        }
        if (phone != null && !phone.isEmpty()) {
            conditions.add("EXISTS (SELECT p FROM PhoneData p WHERE p.user.id = u.id AND p.phone = :phone)");
        }
        if (dateOfBirthAfter != null) {
            conditions.add("u.dateOfBirth > :dateOfBirth");
        }

        if (!conditions.isEmpty()) {
            String whereClause = " WHERE " + String.join(" AND ", conditions);
            baseQuery.append(whereClause);
            countQuery.append(whereClause);
        }

        TypedQuery<User> query = em.createQuery(baseQuery.toString(), User.class);
        TypedQuery<Long> count = em.createQuery(countQuery.toString(), Long.class);

        if (namePrefix != null && !namePrefix.isEmpty()) {
            query.setParameter("namePrefix", namePrefix + "%");
            count.setParameter("namePrefix", namePrefix + "%");
        }
        if (email != null && !email.isEmpty()) {
            query.setParameter("email", email);
            count.setParameter("email", email);
        }
        if (phone != null && !phone.isEmpty()) {
            query.setParameter("phone", phone);
            count.setParameter("phone", phone);
        }
        if (dateOfBirthAfter != null) {
            query.setParameter("dateOfBirth", dateOfBirthAfter);
            count.setParameter("dateOfBirth", dateOfBirthAfter);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<User> users = query.getResultList();
        long total = count.getSingleResult();

        return new PageImpl<>(users, pageable, total);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u JOIN EmailData e ON e.user = u WHERE e.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u JOIN PhoneData p ON p.user = u WHERE p.phone = :phone", User.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
