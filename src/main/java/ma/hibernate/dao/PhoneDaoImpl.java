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
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create a phone: " + phone);
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
            Predicate predicateAnd = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> phoneCriteria = cb.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    phoneCriteria.value(value);
                }
                predicateAnd = cb.and(predicateAnd, phoneCriteria);
            }
            query.where(predicateAnd);
            return session.createQuery(query).getResultList();
        }
    }
}
