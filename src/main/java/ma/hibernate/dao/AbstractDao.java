package ma.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class AbstractDao<T> {
    protected final SessionFactory factory;

    protected AbstractDao(SessionFactory sessionFactory) {
        this.factory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return factory.getCurrentSession();
    }

    public T save(T entity) {
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    public T findById(Class<T> clazz, Long id) {
        return getCurrentSession().get(clazz, id);
    }
}
