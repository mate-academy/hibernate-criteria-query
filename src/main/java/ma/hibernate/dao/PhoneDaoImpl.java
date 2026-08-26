package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create phone "
                    + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        return executeQuery(params);
    }

    private List<Phone> executeQuery(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);

            List<Predicate> predicates = params.entrySet().stream()
                    .flatMap(entry -> createPredicates(root,
                            entry.getKey(),
                            entry.getValue()))
                    .toList();

            cq.where(predicates.toArray(new Predicate[0]));

            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find phones with parameters: "
                    + mapToString(params), e);
        }
    }

    private Stream<Predicate> createPredicates(Root<Phone> root, String key, String[] values) {
        try {
            return Stream.of(values)
                    .filter(value -> values != null && values.length > 0)
                    .map(value -> root.get(key).in((Object[]) values));
        } catch (Exception e) {
            return Stream.empty();
        }
    }

    private String mapToString(Map<String, String[]> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey()
                        + "=["
                        + String.join(", ", entry.getValue())
                        + "]")
                .collect(Collectors.joining(", "));
    }
}
