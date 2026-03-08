package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Staff;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class StaffService {

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

    public List<Staff> findAll() {
        return inTransaction(em ->
                em.createQuery("select s from Staff s order by s.id", Staff.class)
                        .getResultList()
        );
    }

    public Staff create(Staff s) {
        return inTransaction(em -> {
            em.persist(s);
            return s;
        });
    }

    public Staff update(Staff s) {
        return inTransaction(em -> em.merge(s));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            Staff s = em.find(Staff.class, id);
            if (s != null) {
                em.remove(s);
            }
        });
    }
}

