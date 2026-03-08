package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import vn.edu.ute.languagecenter.model.UserAccount;
import vn.edu.ute.languagecenter.model.UserRole;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserAccountService {

    private <R> R inTransaction(Function<EntityManager, R> work) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = work.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    private void inTransactionVoid(Consumer<EntityManager> work) {
        inTransaction(em -> {
            work.accept(em);
            return null;
        });
    }

    public List<UserAccount> findAll() {
        return inTransaction(em ->
                em.createQuery("select u from UserAccount u order by u.id", UserAccount.class)
                        .getResultList()
        );
    }

    public List<UserAccount> findByRole(UserRole role) {
        if (role == null) {
            return findAll();
        }
        String value = role.name();
        return inTransaction(em ->
                em.createQuery("select u from UserAccount u where u.role = :r order by u.id", UserAccount.class)
                        .setParameter("r", value)
                        .getResultList()
        );
    }

    public List<UserAccount> findByRoles(UserRole... roles) {
        if (roles == null || roles.length == 0) {
            return findAll();
        }
        List<String> names = Arrays.stream(roles).map(UserRole::name).collect(Collectors.toList());
        return inTransaction(em ->
                em.createQuery("select u from UserAccount u where u.role in :roles order by u.id", UserAccount.class)
                        .setParameter("roles", names)
                        .getResultList()
        );
    }

    public void setPassword(Long accountId, String rawPassword) {
        inTransactionVoid(em -> {
            UserAccount u = em.find(UserAccount.class, accountId);
            if (u != null) {
                u.setPasswordHash(hashPassword(rawPassword));
            }
        });
    }

    public UserAccount findByUsername(String username) {
        return inTransaction(em -> {
            try {
                return em.createQuery("select u from UserAccount u where u.username = :u", UserAccount.class)
                        .setParameter("u", username)
                        .getSingleResult();
            } catch (NoResultException ex) {
                return null;
            }
        });
    }

    public UserAccount createWithRawPassword(String username, String rawPassword, String role, Long relatedId) {
        UserAccount existing = findByUsername(username);
        if (existing != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        UserAccount u = new UserAccount();
        u.setUsername(username);
        u.setPasswordHash(hashPassword(rawPassword));
        u.setRole(role);
        u.setRelatedId(relatedId);
        u.setStatus("ACTIVE");
        u.setFailedLoginCount(0);
        return create(u);
    }

    public UserAccount createWithRawPassword(String username, String rawPassword, UserRole role, Long relatedId) {
        String value = role != null ? role.name() : null;
        return createWithRawPassword(username, rawPassword, value, relatedId);
    }

    public UserAccount create(UserAccount u) {
        return inTransaction(em -> {
            em.persist(u);
            return u;
        });
    }

    public UserAccount update(UserAccount u) {
        return inTransaction(em -> em.merge(u));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            UserAccount u = em.find(UserAccount.class, id);
            if (u != null) {
                em.remove(u);
            }
        });
    }

    public void recordSuccessfulLogin(UserAccount u) {
        inTransactionVoid(em -> {
            UserAccount managed = em.find(UserAccount.class, u.getId());
            if (managed == null) {
                return;
            }
            managed.setLastLogin(LocalDateTime.now());
            managed.setFailedLoginCount(0);
            managed.setLockoutUntil(null);
        });
    }

    public void recordFailedLogin(UserAccount u, int maxFailedAttempts, int lockMinutes) {
        inTransactionVoid(em -> {
            UserAccount managed = em.find(UserAccount.class, u.getId());
            if (managed == null) {
                return;
            }
            Integer count = managed.getFailedLoginCount();
            if (count == null) {
                count = 0;
            }
            count = count + 1;
            managed.setFailedLoginCount(count);
            if (count >= maxFailedAttempts) {
                managed.setLockoutUntil(LocalDateTime.now().plusMinutes(lockMinutes));
            }
        });
    }

    public boolean isLocked(UserAccount u) {
        LocalDateTime until = u.getLockoutUntil();
        if (until == null) {
            return false;
        }
        return until.isAfter(LocalDateTime.now());
    }

    public boolean verifyPassword(UserAccount u, String rawPassword) {
        String hash = hashPassword(rawPassword);
        return hash.equals(u.getPasswordHash());
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

