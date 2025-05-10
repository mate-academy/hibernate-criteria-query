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
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            JpaRoot<Phone> phoneRoot = query.from(Phone.class);
            List<JpaInPredicate<String>> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                JpaInPredicate<String> keyPredicate = cb.in(phoneRoot.get(entry.getKey()));
                for (String item : entry.getValue()) {
                    keyPredicate.value(item);
                }
                predicates.add(keyPredicate);
            }
            query.where(cb.and(predicates.toArray(new JpaInPredicate[0])));
            return session.createQuery(query).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Can't find params ", e);
        }
    }
}
