package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
            throw new RuntimeException("Can't add phone to db", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            Predicate prevPredicate = null;
            boolean flag = true;

            for (Map.Entry<String, String[]> en : params.entrySet()) {
                CriteriaBuilder.In<String> paramPredicate = cb.in(phoneRoot.get(en.getKey()));
                for (String s : en.getValue()) {
                    paramPredicate.value(s);
                }
                prevPredicate = flag ? paramPredicate : cb.and(prevPredicate, paramPredicate);
                flag = false;
            }

            query = prevPredicate == null ? query : query.where(cb.and(prevPredicate));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get phones by params from db", e);
        }
    }
}
