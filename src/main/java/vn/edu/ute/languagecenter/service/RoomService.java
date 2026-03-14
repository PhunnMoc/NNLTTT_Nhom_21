package vn.edu.ute.languagecenter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.edu.ute.languagecenter.model.Room;
import vn.edu.ute.languagecenter.persistence.JpaUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RoomService {

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

    public List<Room> findAll() {
        return inTransaction(em ->
                em.createQuery("select r from Room r order by r.id", Room.class)
                        .getResultList()
        );
    }

    public List<Room> findByStatus(String status) {
        if (status == null || status.isBlank()) return findAll();
        return inTransaction(em ->
                em.createQuery("select r from Room r where r.status = :st order by r.id", Room.class)
                        .setParameter("st", status)
                        .getResultList()
        );
    }

    public List<Room> search(String keyword, String status) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        String st = status == null || status.isBlank() ? null : status;
        return inTransaction(em -> {
            String jpql = "select r from Room r where (:kw = '' or lower(r.roomName) like :kw or lower(r.location) like :kw) and (:st is null or r.status = :st) order by r.id";
            return em.createQuery(jpql, Room.class)
                    .setParameter("kw", "%" + kw + "%")
                    .setParameter("st", st)
                    .getResultList();
        });
    }

    public Room create(Room r) {
        return inTransaction(em -> {
            em.persist(r);
            return r;
        });
    }

    public Room update(Room r) {
        return inTransaction(em -> em.merge(r));
    }

    public void deleteById(Long id) {
        inTransactionVoid(em -> {
            Room r = em.find(Room.class, id);
            if (r != null) {
                em.remove(r);
            }
        });
    }
}
