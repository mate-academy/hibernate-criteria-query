package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone" + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = cq.from(Phone.class);

            final Predicate[] finalPredicate = {cb.conjunction()}; // Use an array

            params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .forEach(entry -> {
                        String paramKey = entry.getKey();
                        String[] paramValues = entry.getValue();
                        Expression<String> paramExpression = phoneRoot.get(paramKey);
                        Predicate paramPredicate = paramExpression.in((Object[]) paramValues);
                        finalPredicate[0] = cb.and(finalPredicate[0], paramPredicate);
                    });

            cq.where(finalPredicate[0]);
            Query<Phone> query = session.createQuery(cq);

            return query.getResultList();
        } finally {
            session.close();
        }
    }
}
