package ma.hibernate.dao;

import org.hibernate.SessionFactory;

public abstract class AbstractDao {
    private final SessionFactory sessionFactory;

    protected AbstractDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
