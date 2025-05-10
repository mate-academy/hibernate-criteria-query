package ma.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class AbstractDao<T> {
    protected final SessionFactory factory;

    protected AbstractDao(SessionFactory sessionFactory) {
        this.factory = sessionFactory;
    }

    T create(T entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Couldn't create entity", exception);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
