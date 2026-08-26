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

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try {
            factory.inTransaction(session -> session.persist(phone));
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't add this phone entity to db: " + phone, e);
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            CriteriaBuilder.In<String> paramsPredicate;
            Predicate combinedPredicate = cb.conjunction();
            for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
                paramsPredicate = cb.in(phoneRoot.get(stringEntry.getKey()));
                for (String s : stringEntry.getValue()) {
                    paramsPredicate.value(s);
                }
                combinedPredicate = cb.and(combinedPredicate, paramsPredicate);
            }
            criteriaQuery.where(combinedPredicate);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't create findAll query with this params: " + params, e);
        }
    }
}
