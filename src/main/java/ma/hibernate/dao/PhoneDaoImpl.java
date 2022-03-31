package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Couldn't create phone:" + phone, e);
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
            Predicate wherePredicate = null;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> in = cb.in(root.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    in.value(value);
                }
                if (wherePredicate == null) {
                    wherePredicate = in;
                } else {
                    wherePredicate = cb.and(wherePredicate, in);
                }
            }
            if (wherePredicate != null) {
                query.where(wherePredicate);
            }
            return session.createQuery(query).getResultList();
        }
    }
}
