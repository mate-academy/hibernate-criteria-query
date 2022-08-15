package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.HibernateException;
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
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
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
            CriteriaQuery<Phone> findAllWithParamsQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllWithParamsQuery.from(Phone.class);
            Predicate totalPredicate = null;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> paramPredicate = cb.in(phoneRoot.get(entry.getKey()));
                Arrays.stream(entry.getValue()).forEach(paramPredicate::value);
                if (totalPredicate == null) {
                    totalPredicate = paramPredicate;
                } else {
                    totalPredicate = cb.and(totalPredicate, paramPredicate);
                }
            }
            if (totalPredicate != null) {
                findAllWithParamsQuery.where(totalPredicate);
            }
            return session.createQuery(findAllWithParamsQuery).getResultList();
        }
    }
}
