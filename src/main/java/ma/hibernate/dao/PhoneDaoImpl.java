package ma.hibernate.dao;

import java.util.ArrayList;
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
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone " + phone, exception);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> inPredicate =
                        criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    inPredicate.value(value);
                }
                predicates.add(inPredicate);
            }
            query.where(criteriaBuilder.and(predicates
                    .toArray(new Predicate[predicates.size()])));
            return session.createQuery(query).getResultList();
        } catch (Exception exception) {
            String phoneParameters = params.entrySet().stream()
                    .map(entry -> entry.getKey() + ": "
                            + String.join(", ", entry.getValue())
                            + System.lineSeparator())
                    .collect(Collectors.joining());
            throw new RuntimeException("Can't get list of phones with selected parameters: "
                    + System.lineSeparator()
                    + phoneParameters, exception);
        }
    }
}
