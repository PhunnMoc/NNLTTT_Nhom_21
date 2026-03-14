package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Course;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<Course> findCoursesByTeacherId(Long teacherId) {
        if (teacherId == null) return List.of();
        List<Course> list = inTransaction(em ->
                em.createQuery(
                        "select distinct c from CourseClass cc join cc.course c where cc.teacher.id = :tid",
                        Course.class)
                        .setParameter("tid", teacherId)
                        .getResultList()
        );
        return list.stream().sorted(Comparator.comparing(Course::getId)).collect(Collectors.toList());
    }

    public List<Course> findCoursesByStudentId(Long studentId) {
        if (studentId == null) return List.of();
        List<Course> list = inTransaction(em ->
                em.createQuery(
                        "select distinct c from Enrollment e join e.courseClass cc join cc.course c where e.student.id = :sid",
                        Course.class)
                        .setParameter("sid", studentId)
                        .getResultList()
        );
        return list.stream().sorted(Comparator.comparing(Course::getId)).collect(Collectors.toList());
    }
}

