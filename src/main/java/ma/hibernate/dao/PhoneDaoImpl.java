package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Failed to create the new phone: " + phone, e);
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

            Predicate makerPredicate = buildEqualPredicate(params, "maker", "maker", cb, phoneRoot);
            Predicate colorPredicate = buildEqualPredicate(params, "color", "color", cb, phoneRoot);
            Predicate modelPredicate = buildEqualPredicate(params, "model", "model", cb, phoneRoot);
            Predicate countryPredicate = buildEqualPredicate(params, "countryManufactured",
                    "countryManufactured", cb, phoneRoot);

            query.where(cb.and(makerPredicate, colorPredicate, modelPredicate, countryPredicate));
            return session.createQuery(query).getResultList();
        }
    }

    private Predicate buildEqualPredicate(Map<String, String[]> params, String paramKey,
                                          String fieldName, CriteriaBuilder cb, Root<Phone> root) {
        return Arrays.stream(params.getOrDefault(paramKey, new String[0]))
                .filter(Objects::nonNull)
                .map(value -> cb.equal(root.get(fieldName), value))
                .reduce(cb::or)
                .orElse(cb.conjunction());
    }
}
