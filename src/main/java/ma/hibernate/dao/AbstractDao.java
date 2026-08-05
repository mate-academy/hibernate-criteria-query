package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public abstract class AbstractDao<T> {
    protected final SessionFactory factory;

    protected AbstractDao(SessionFactory sessionFactory) {
        this.factory = sessionFactory;
    }

    protected Object create(Object entity) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add entity " + entity.getClass(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return entity;
    }

    protected List<T> findAll(Map<String, String[]> params, Class clazz) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<T> query = criteriaBuilder.createQuery(clazz);
            Root<T> root = query.from(clazz);
            List<CriteriaBuilder.In> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> predicate = criteriaBuilder.in(root.get(entry.getKey()));
                for (String info : entry.getValue()) {
                    predicate.value(info);
                }
                predicates.add(predicate);
            }
            CriteriaBuilder.In<Object>[] criteria = predicates.toArray(new CriteriaBuilder.In[0]);
            query.where(criteriaBuilder.and(criteria));
            return session.createQuery(query).getResultList();
        }
    }
}

