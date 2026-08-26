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
    private final SessionFactory sessionFactory;

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Phone create(Phone phone) {
        Session session = sessionFactory.openSession();
        session.save(phone);
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .map(entry -> {
                        CriteriaBuilder.In<String> inPredicate = cb.in(root.get(entry.getKey()));
                        java.util.Arrays.stream(entry.getValue()).forEach(inPredicate::value);
                        return inPredicate;
                    })
                    .collect(java.util.stream.Collectors.toList());
            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        }
    }
}
