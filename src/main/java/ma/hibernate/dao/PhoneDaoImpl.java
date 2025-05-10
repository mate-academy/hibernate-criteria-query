package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
            throw new RuntimeException("Can't insert phone " + phone, e);
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate andPredicate = null;
            for (Map.Entry<String, String[]> param: params.entrySet()) {
                String key = param.getKey();
                String[] values = param.getValue();
                CriteriaBuilder.In<String> paramPredicate = cb.in(phoneRoot.get(key));
                for (String value : values) {
                    paramPredicate.value(value);
                }
                andPredicate = andPredicate == null ? paramPredicate
                        : cb.and(andPredicate, paramPredicate);
            }
            query.where(andPredicate != null ? andPredicate : cb.conjunction());
            return session.createQuery(query).getResultList();
        }
    }
}
