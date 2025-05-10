package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        SessionFactory sessionFactory = PhoneDaoImpl.super.factory;
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            Optional.ofNullable(transaction).ifPresent(Transaction::rollback);
            throw new RuntimeException("Can't save product to DB", e);
        } finally {
            Optional.ofNullable(session).ifPresent(Session::close);
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {

        SessionFactory sessionFactory = PhoneDaoImpl.super.factory;
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> currentPredicate = cb.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    currentPredicate.value(value);
                }
                predicates.add(currentPredicate);
            }
            Optional.of(predicates)
                    .filter(p -> !p.isEmpty())
                    .ifPresent(p -> query.where(cb.and(p.toArray(new Predicate[0]))));

            return session.createQuery(query).getResultList();
        }
    }
}
