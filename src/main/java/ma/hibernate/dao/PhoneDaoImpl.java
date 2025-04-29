package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    private static final int SINGLE_VALUE = 1;
    private static final int FIRST_ELEMENT_INDEX = 0;
    private static final Predicate[] EMPTY_PREDICATE_ARRAY = new Predicate[0];

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        return executeTransaction(session -> {
            session.persist(phone);
            return phone;
        });
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        return executeTransaction(session -> {
            CriteriaQuery<Phone> query = buildCriteriaQuery(session, params);
            return session.createQuery(query).getResultList();
        });
    }

    private CriteriaQuery<Phone> buildCriteriaQuery(Session session, Map<String, String[]> params) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
        Root<Phone> phoneRoot = query.from(Phone.class);
        List<Predicate> predicates = buildPredicates(params, cb, phoneRoot);
        query.select(phoneRoot).where(cb.and(predicates.toArray(EMPTY_PREDICATE_ARRAY)));
        return query;
    }

    private List<Predicate> buildPredicates(Map<String, String[]> params, CriteriaBuilder cb,
                                            Root<Phone> root) {
        return params.entrySet().stream()
                .map(entry -> {
                    var key = entry.getKey();
                    var values = entry.getValue();
                    return values.length == SINGLE_VALUE
                        ? cb.equal(root.get(key), values[FIRST_ELEMENT_INDEX])
                        : root.get(key).in((Object[]) values);
                })
                .collect(Collectors.toList());
    }
}
