package ma.hibernate.dao;

import java.util.ArrayList;
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
            throw new RuntimeException(
                    "Can't create phone " + phone, e);
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
            Root<Phone> root = query.from(Phone.class);
            Predicate commonPredicate = null;
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                CriteriaBuilder.In<String> paramPredicate = cb.in(root.get(param.getKey()));
                for (String value : param.getValue()) {
                    paramPredicate.value(value);
                }
                predicates.add(paramPredicate);
            }
            commonPredicate = cb.and(predicates.toArray(predicates.toArray(new Predicate[0])));
            if (commonPredicate != null) {
                query.where(commonPredicate);
            }
            return session.createQuery(query).getResultList();
        } catch (HibernateException e) {
            throw new RuntimeException(
                    "Can't get requested phones from DB", e);
        }
    }
}
