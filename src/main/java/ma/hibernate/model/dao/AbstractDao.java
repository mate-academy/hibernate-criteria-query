package ma.hibernate.model.dao;

import org.hibernate.SessionFactory;

public abstract class AbstractDao {
    protected final SessionFactory factory;

    protected AbstractDao(SessionFactory sessionFactory) {
        this.factory = sessionFactory;
    }
}
