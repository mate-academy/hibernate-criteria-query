package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try (var session = factory.openSession()) {
            var tx = session.beginTransaction();
            session.persist(phone);
            tx.commit();
            return phone;
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        // Rozszerzone mapowanie klucz_parametru -> pole_encji
        Map<String, String> fieldMap = Map.of(
                "producer", "maker",
                "maker", "maker",
                "model", "model",
                "color", "color",
                "countryManufactured", "countryManufactured",
                "os", "os"
        );

        try (var session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);

            Predicate[] predicates = params.entrySet().stream()
                    // uwzględniamy tylko te parametry, które mapujemy na pola encji
                    .filter(e -> fieldMap.containsKey(e.getKey()))
                    // oraz tylko te z wartością niepustą
                    .filter(e -> e.getValue() != null && e.getValue().length > 0)
                    .map(e -> {
                        String field = fieldMap.get(e.getKey());
                        String[] vals = e.getValue();
                        // operator ?: zamiast if/else
                        return vals.length == 1
                                ? cb.equal(root.get(field), vals[0])
                                : root.get(field).in((Object[]) vals);
                    })
                    .toArray(Predicate[]::new);

            cq.select(root).where(cb.and(predicates));
            return session.createQuery(cq).getResultList();
        }
    }
}
