package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Teacher;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TeacherService {

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

    public List<Teacher> findAll() {
        return inTransaction(em ->
                em.createQuery("select t from Teacher t order by t.id", Teacher.class)
                        .getResultList()
        );
    }

    public Teacher create(Teacher t) {
        return inTransaction(em -> {
            em.persist(t);
            return t;
        });
    }

    public Teacher update(Teacher t) {
        return inTransaction(em -> em.merge(t));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            Teacher t = em.find(Teacher.class, id);
            if (t != null) {
                em.remove(t);
            }
        });
    }
}

