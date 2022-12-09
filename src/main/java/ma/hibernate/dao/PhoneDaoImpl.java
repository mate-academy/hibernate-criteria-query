package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
            throw new RuntimeException("Can't create new phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate predicate = null;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> currentPredicate = cb.in(phoneRoot.get(entry.getKey()));
                Arrays.stream(entry.getValue()).forEach(currentPredicate::value);
                predicate = predicate != null
                    ? cb.and(predicate, currentPredicate)
                    : currentPredicate;
            }
            query = predicate != null
                    ? query.where(predicate)
                    : query;
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get phones with params: " + params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", ")));
        }
    }
}
