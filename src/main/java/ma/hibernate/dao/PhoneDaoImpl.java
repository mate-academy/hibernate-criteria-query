package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert a phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            if (params != null && !params.isEmpty()) {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
                Root<Phone> root = query.from(Phone.class);
                // Predicates
                Predicate predicate = null;
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    CriteriaBuilder.In<String> tempPredicate = criteriaBuilder.in(root.get(key));
                    Arrays.stream(params.get(key)).forEach(tempPredicate::value);
                    if (predicate == null) {
                        predicate = tempPredicate;
                    } else {
                        predicate = criteriaBuilder.and(predicate, tempPredicate);
                    }
                }
                query.where(predicate);
                return session.createQuery(query).getResultList();
            }
            return session.createQuery("from Phone", Phone.class).getResultList();
        }
    }
}
