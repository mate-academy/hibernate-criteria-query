package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaInPredicate;
import org.hibernate.query.criteria.JpaRoot;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            JpaRoot<Phone> phoneJpaRoot = query.from(Phone.class);
            List<JpaInPredicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                JpaInPredicate<String> inPredicate
                        = criteriaBuilder.in(phoneJpaRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    inPredicate.value(value);
                }
                predicates.add(inPredicate);
            }
            query.where(criteriaBuilder.and(predicates.toArray(JpaInPredicate[]::new)));
            return session.createQuery(query).getResultList();
        }
    }
}
