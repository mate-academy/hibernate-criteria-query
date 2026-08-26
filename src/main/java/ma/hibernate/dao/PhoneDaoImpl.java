package ma.hibernate.dao;

import jakarta.persistence.criteria.Predicate;
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
        Session session = null;
        Transaction transaction = null;
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
            throw new RuntimeException("Can't add phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            JpaRoot<Phone> root = query.from(Phone.class);

            List<JpaInPredicate<String>> predicatesList = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                JpaInPredicate<String> inPredicate = cb.in(root.get(entry.getKey()));
                for (String values : entry.getValue()) {
                    inPredicate.value(values);
                }
                predicatesList.add(inPredicate);
            }
            query.where(cb.and(predicatesList.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        }
    }
}
