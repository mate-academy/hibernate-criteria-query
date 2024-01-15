package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
            throw new RuntimeException("Can't save phone to DB", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        session.close();
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            params.forEach((k, v) -> {
                CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(k));
                for (String vvalue : v) {
                    predicate.value(vvalue);
                }
                predicates.add(predicate);
            });
            Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            query.where(finalPredicate);
            return session.createQuery(query).getResultList();
        }
    }
}
