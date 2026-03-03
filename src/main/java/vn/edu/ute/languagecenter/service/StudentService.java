package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Student;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class StudentService {

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

    public List<Student> findAll() {
        return inTransaction(em ->
                em.createQuery("select s from Student s order by s.id", Student.class)
                        .getResultList()
        );
    }

    public Student create(Student s) {
        return inTransaction(em -> {
            em.persist(s);
            return s;
        });
    }

    public Student update(Student s) {
        return inTransaction(em -> em.merge(s));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            Student s = em.find(Student.class, id);
            if (s != null) {
                em.remove(s);
            }
        });
    }
}

