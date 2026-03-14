package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Schedule;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ScheduleService {

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

    public List<Schedule> findByClassId(Long classId) {
        if (classId == null) return List.of();
        return inTransaction(em ->
                em.createQuery("select s from Schedule s left join fetch s.courseClass left join fetch s.room where s.courseClass.id = :cid order by s.date, s.startTime", Schedule.class)
                        .setParameter("cid", classId)
                        .getResultList()
        );
    }

    public Schedule create(Schedule s) {
        return inTransaction(em -> {
            em.persist(s);
            return s;
        });
    }

    public Schedule update(Schedule s) {
        return inTransaction(em -> em.merge(s));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            Schedule s = em.find(Schedule.class, id);
            if (s != null) {
                em.remove(s);
            }
        });
    }

    public void deleteByClassId(Long classId) {
        if (classId == null) return;
        inTransactionVoid(em -> {
            em.createQuery("delete from Schedule s where s.courseClass.id = :cid")
                    .setParameter("cid", classId)
                    .executeUpdate();
        });
    }

    public boolean existsRoomConflict(Long roomId, java.time.LocalDate date, java.time.LocalTime start, java.time.LocalTime end) {
        if (roomId == null || date == null || start == null || end == null) return false;
        return inTransaction(em -> {
            String jpql = "select count(s) from Schedule s where s.room.id = :rid and s.date = :d and s.startTime < :end and s.endTime > :start";
            Long cnt = em.createQuery(jpql, Long.class)
                    .setParameter("rid", roomId)
                    .setParameter("d", date)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getSingleResult();
            return cnt != null && cnt > 0;
        });
    }

    public List<Schedule> findByRoomAndRange(Long roomId, java.time.LocalDate from, java.time.LocalDate to) {
        if (roomId == null || from == null || to == null) return List.of();
        return inTransaction(em ->
                em.createQuery(
                                "select s from Schedule s left join fetch s.courseClass c left join fetch c.course left join fetch c.teacher where s.room.id = :rid and s.date between :from and :to order by s.date, s.startTime",
                                Schedule.class)
                        .setParameter("rid", roomId)
                        .setParameter("from", from)
                        .setParameter("to", to)
                        .getResultList()
        );
    }
}
