package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Teacher;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClassService {

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

    public List<CourseClass> findAll() {
        return inTransaction(em ->
                em.createQuery("select cc from CourseClass cc left join fetch cc.course left join fetch cc.teacher left join fetch cc.room order by cc.id", CourseClass.class)
                        .getResultList()
        );
    }

    public CourseClass findById(Long id) {
        if (id == null) return null;
        return inTransaction(em ->
                em.createQuery("select cc from CourseClass cc left join fetch cc.course left join fetch cc.teacher left join fetch cc.room where cc.id = :id", CourseClass.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst()
                        .orElse(null)
        );
    }

    public List<CourseClass> findByCourseId(Long courseId) {
        if (courseId == null) return List.of();
        return inTransaction(em ->
                em.createQuery("select cc from CourseClass cc left join fetch cc.course left join fetch cc.teacher left join fetch cc.room where cc.course.id = :cid order by cc.startDate desc", CourseClass.class)
                        .setParameter("cid", courseId)
                        .getResultList()
        );
    }

    public List<CourseClass> findByTeacherId(Long teacherId) {
        if (teacherId == null) return List.of();
        return inTransaction(em ->
                em.createQuery("select cc from CourseClass cc left join fetch cc.course left join fetch cc.teacher left join fetch cc.room where cc.teacher.id = :tid order by cc.startDate desc", CourseClass.class)
                        .setParameter("tid", teacherId)
                        .getResultList()
        );
    }

    public int countEnrolled(Long classId) {
        if (classId == null) return 0;
        Long count = inTransaction(em ->
                em.createQuery("select count(e) from Enrollment e where e.courseClass.id = :cid", Long.class)
                        .setParameter("cid", classId)
                        .getSingleResult()
        );
        return count != null ? count.intValue() : 0;
    }

    public List<Teacher> findTeachersAvailableForPeriod(LocalDate start, LocalDate end, Long excludeClassId) {
        if (start == null || end == null) {
            return inTransaction(em ->
                    em.createQuery("select t from Teacher t order by t.id", Teacher.class).getResultList()
            );
        }
        Long excludeId = excludeClassId != null ? excludeClassId : -1L;
        return inTransaction(em ->
                em.createQuery(
                        "select t from Teacher t where not exists (" +
                                "select 1 from CourseClass cc where cc.teacher = t and (cc.id <> :excludeId) " +
                                "and cc.startDate <= :end and cc.endDate >= :start)",
                        Teacher.class)
                        .setParameter("excludeId", excludeId)
                        .setParameter("start", start)
                        .setParameter("end", end)
                        .getResultList()
        );
    }

    public boolean isTeacherAvailable(Long teacherId, LocalDate start, LocalDate end, Long excludeClassId) {
        if (teacherId == null || start == null || end == null) return true;
        Long count = inTransaction(em -> {
            String jpql = "select count(cc) from CourseClass cc where cc.teacher.id = :tid " +
                    "and cc.startDate <= :end and cc.endDate >= :start";
            if (excludeClassId != null) {
                jpql += " and cc.id <> :excludeId";
            }
            var q = em.createQuery(jpql, Long.class)
                    .setParameter("tid", teacherId)
                    .setParameter("start", start)
                    .setParameter("end", end);
            if (excludeClassId != null) {
                q.setParameter("excludeId", excludeClassId);
            }
            return q.getSingleResult();
        });
        return count != null && count == 0;
    }

    public CourseClass create(CourseClass cc) {
        return inTransaction(em -> {
            em.persist(cc);
            return cc;
        });
    }

    public CourseClass update(CourseClass cc) {
        return inTransaction(em -> em.merge(cc));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            CourseClass cc = em.find(CourseClass.class, id);
            if (cc != null) {
                em.remove(cc);
            }
        });
    }
}
