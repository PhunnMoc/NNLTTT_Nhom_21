package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Payment;
import vn.edu.ute.languagecenter.model.PaymentStatus;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaymentService {

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

    public Payment create(Payment p) {
        return inTransaction(em -> {
            em.persist(p);
            return p;
        });
    }

    public List<Payment> findByEnrollmentId(Long enrollmentId) {
        if (enrollmentId == null) {
            return List.of();
        }
        return inTransaction(em ->
                em.createQuery("select p from Payment p where p.enrollment.id = :eid order by p.paymentDate", Payment.class)
                        .setParameter("eid", enrollmentId)
                        .getResultList()
        );
    }

    public List<Payment> findAll() {
        return inTransaction(em ->
                em.createQuery(
                                "select p from Payment p " +
                                        "join fetch p.student s " +
                                        "left join fetch p.enrollment e " +
                                        "left join fetch e.courseClass cc " +
                                        "left join fetch cc.course c " +
                                        "order by p.paymentDate desc",
                                Payment.class)
                        .getResultList()
        );
    }

    public List<Payment> findByStudentId(Long studentId) {
        if (studentId == null) {
            return List.of();
        }
        return inTransaction(em ->
                em.createQuery(
                                "select p from Payment p " +
                                        "join fetch p.student s " +
                                        "left join fetch p.enrollment e " +
                                        "left join fetch e.courseClass cc " +
                                        "left join fetch cc.course c " +
                                        "where s.id = :sid " +
                                        "order by p.paymentDate desc",
                                Payment.class)
                        .setParameter("sid", studentId)
                        .getResultList()
        );
    }

    public void updateStatus(Long id, PaymentStatus status) {
        if (id == null || status == null) {
            return;
        }
        inTransactionVoid(em -> {
            Payment p = em.find(Payment.class, id);
            if (p != null) {
                p.setStatus(status.toString());
            }
        });
    }
}

