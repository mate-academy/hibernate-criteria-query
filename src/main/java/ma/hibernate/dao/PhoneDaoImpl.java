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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone to DB: " + phone, e);
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
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            JpaRoot<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            List<JpaInPredicate<Object>> predicateList = new ArrayList<>(params.size());
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                JpaInPredicate<Object> somePredicate = cb.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    somePredicate.value(value);
                }
                predicateList.add(somePredicate);
            }
            JpaInPredicate<Phone>[] predicatesArray = predicateList.toArray(new JpaInPredicate[0]);
            criteriaQuery.where(predicatesArray);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
