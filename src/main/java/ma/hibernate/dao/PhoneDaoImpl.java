package ma.hibernate.dao;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create phone: " + phone.toString());
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

            List<Predicate> predicates = Optional.ofNullable(params)
                    .orElse(Collections.emptyMap())
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        CriteriaBuilder.In<String> inPredicate =
                                cb.in(phoneRoot.get(entry.getKey()));
                        Arrays.stream(
                                        entry.getValue())
                                .forEach(val -> inPredicate.value(val));
                        return inPredicate;
                    })
                    .collect(Collectors.toList());

            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();

        } catch (Exception ex) {
            throw new RuntimeException("Failed to find phones with params: " + params.toString());
        }
    }
}
