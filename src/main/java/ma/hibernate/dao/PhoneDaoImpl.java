package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Cant save phone to db", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
        Root<Phone> phoneRoot = query.from(Phone.class);
        Predicate predicate = cb.conjunction();
        Set<Map.Entry<String, String[]>> entries = params.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            CriteriaBuilder.In<Object> paramPredicate = cb.in(phoneRoot.get(entry.getKey()));
            for (String param : entry.getValue()) {
                paramPredicate.value(param);
            }
            predicate = cb.and(predicate, paramPredicate);
        }
        query.where(predicate);
        return session.createQuery(query).getResultList();
    }

}
