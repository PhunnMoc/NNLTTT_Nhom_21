package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Course;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CourseService {

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

    public List<Course> findAll() {
        return inTransaction(em ->
                em.createQuery("select c from Course c order by c.id", Course.class)
                        .getResultList()
        );
    }

    public Course create(Course c) {
        return inTransaction(em -> {
            em.persist(c);
            return c;
        });
    }

    public Course update(Course c) {
        return inTransaction(em -> em.merge(c));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            Course c = em.find(Course.class, id);
            if (c != null) {
                em.remove(c);
            }
        });
    }
}

