package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl implements PhoneDao {
    private final SessionFactory sessionFactory;

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);

            Map<String, Function<String[], Predicate>> predicateMap = Map.of(
                    "countryManufactured", values
                            -> root.get("countryManufactured").in(Arrays.asList(values)),
                    "maker", values -> cb.lower(root.get("maker")).in(Arrays.stream(values)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList())),
                    "color", values -> cb.lower(root.get("color")).in(Arrays.stream(values)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList())),
                    "model", values -> root.get("model").in(Arrays.asList(values))
            );

            List<Predicate> predicates = params.entrySet().stream()
                    .map(entry -> Optional.ofNullable(predicateMap.get(entry.getKey()))
                            .map(func -> func.apply(entry.getValue()))
                            .orElseThrow(()
                                    -> new IllegalArgumentException("Unexpected filter key: "
                                    + entry.getKey())))
                    .collect(Collectors.toList());

            query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't retrieve phones with parameters: "
                    + formatParams(params), e);
        }
    }

    private String formatParams(Map<String, String[]> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
