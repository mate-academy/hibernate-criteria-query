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
            Root<Phone> root = query.from(Phone.class);
            Predicate predicateAll = null;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicateAll = fillPredicates(cb, predicateAll,
                        cb.in(root.get(entry.getKey())), entry.getValue());
            }
            CriteriaQuery<Phone> phoneCriteriaQuery = (params.isEmpty())
                    ? query.select(root)
                    : query.where(predicateAll);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Can't get phones", ex);
        }
    }

    private Predicate fillPredicates(CriteriaBuilder cb, Predicate predicateResult,
                                     CriteriaBuilder.In<String> predicateParams,
                                     String[] paramValues) {
        for (String item : paramValues) {
            predicateParams.value(item);
        }
        return (predicateResult != null)
                ? cb.and(predicateResult, predicateParams)
                : cb.and(predicateParams);
    }
}
