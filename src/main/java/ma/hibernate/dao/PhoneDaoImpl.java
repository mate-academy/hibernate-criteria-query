package ma.hibernate.dao;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            JpaRoot<Phone> phoneJpaRoot = query.from(Phone.class);
            Predicate[] predicates = params.entrySet().stream()
                    .map(entry -> phoneJpaRoot.get(entry.getKey()).in((Object[]) entry.getValue()))
                    .toArray(Predicate[]::new);
            query.where(predicates);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
