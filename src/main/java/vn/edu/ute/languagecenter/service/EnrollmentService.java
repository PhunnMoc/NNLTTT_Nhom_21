package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Enrollment;
import vn.edu.ute.languagecenter.model.EnrollmentStatus;
import vn.edu.ute.languagecenter.model.PaymentStatus;
import vn.edu.ute.languagecenter.model.Student;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class EnrollmentService {

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

    public Enrollment enroll(Long studentId, Long classId) {
        if (studentId == null || classId == null) {
            throw new IllegalArgumentException("studentId and classId must not be null");
        }
        return inTransaction(em -> {
            Student s = em.getReference(Student.class, studentId);
            CourseClass cc = em.getReference(CourseClass.class, classId);
            Enrollment e = new Enrollment();
            e.setStudent(s);
            e.setCourseClass(cc);
            e.setEnrollmentDate(LocalDate.now());
            e.setStatus(EnrollmentStatus.PENDING.toString());
            em.persist(e);
            return e;
        });
    }

    public boolean existsByStudentAndClass(Long studentId, Long classId) {
        if (studentId == null || classId == null) {
            return false;
        }
        Long count = inTransaction(em ->
                em.createQuery(
                                "select count(e) from Enrollment e where e.student.id = :sid and e.courseClass.id = :cid",
                                Long.class)
                        .setParameter("sid", studentId)
                        .setParameter("cid", classId)
                        .getSingleResult()
        );
        return count != null && count > 0;
    }

    public List<Enrollment> findByStudentId(Long studentId) {
        if (studentId == null) {
            return List.of();
        }
        return inTransaction(em ->
                em.createQuery(
                                "select e from Enrollment e " +
                                        "join fetch e.courseClass cc " +
                                        "join fetch cc.course c " +
                                        "left join fetch cc.teacher t " +
                                        "left join fetch cc.room r " +
                                        "where e.student.id = :sid " +
                                        "order by e.enrollmentDate desc",
                                Enrollment.class)
                        .setParameter("sid", studentId)
                        .getResultList()
        );
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            Enrollment e = em.find(Enrollment.class, id);
            if (e != null) {
                em.remove(e);
            }
        });
    }

    public List<Enrollment> findAll() {
        return inTransaction(em ->
                em.createQuery(
                                "select e from Enrollment e " +
                                        "join fetch e.student s " +
                                        "join fetch e.courseClass cc " +
                                        "join fetch cc.course c " +
                                        "left join fetch cc.teacher t " +
                                        "left join fetch cc.room r " +
                                        "order by e.enrollmentDate desc",
                                Enrollment.class)
                        .getResultList()
        );
    }

    public void updateStatus(Long id, EnrollmentStatus status) {
        if (id == null || status == null) {
            return;
        }
        inTransactionVoid(em -> {
            Enrollment e = em.find(Enrollment.class, id);
            if (e != null) {
                e.setStatus(status.toString());
                String paymentStatus = null;
                if (status == EnrollmentStatus.CONFIRMED) {
                    paymentStatus = PaymentStatus.PAID.toString();
                } else if (status == EnrollmentStatus.CANCELLED) {
                    paymentStatus = PaymentStatus.CANCELLED.toString();
                }
                if (paymentStatus != null) {
                    em.createQuery("update Payment p set p.status = :ps where p.enrollment.id = :eid")
                            .setParameter("ps", paymentStatus)
                            .setParameter("eid", id)
                            .executeUpdate();
                }
            }
        });
    }
}

