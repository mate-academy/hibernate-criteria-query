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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            Predicate combinePredicate = null;
            for (Map.Entry<String, String[]> paramSet : params.entrySet()) {
                CriteriaBuilder.In<String> paramPredicate =
                        cb.in(phoneRoot.get(paramSet.getKey()));
                for (String value : paramSet.getValue()) {
                    paramPredicate.value(value);
                }
                if (combinePredicate == null) {
                    combinePredicate = paramPredicate;
                    continue;
                }
                combinePredicate = cb.and(combinePredicate, paramPredicate);
            }
            if (combinePredicate != null) {
                query.where(combinePredicate);
            }
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't find phones with params: "
                    + params.toString(), e);
        }
    }
}
