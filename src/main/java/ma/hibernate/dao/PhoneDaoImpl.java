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
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            try {
                session.persist(phone);
                session.getTransaction().commit();
                return phone;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new RuntimeException("Can't save phone: " + phone, e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during phone creation for: " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = cq.from(Phone.class);

            Predicate[] predicatesArray = params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null
                            && entry.getValue().length > 0)

                    .map(entry -> {
                        String fieldName = entry.getKey();
                        String[] values = entry.getValue();

                        CriteriaBuilder.In<Object> inPredicate = cb.in(phoneRoot.get(fieldName));
                        for (String value : values) {
                            inPredicate.value(value);
                        }
                        return inPredicate;
                    })
                    .toArray(Predicate[]::new);

            Predicate finalPredicate = cb.and(predicatesArray);
            cq.where(finalPredicate);

            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones by params: " + params, e);
        }
    }
}
