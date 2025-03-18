package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can not save phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        CriteriaBuilder criteriaBuilder = factory.getCriteriaBuilder();
        CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
        Root<Phone> root = query.from(Phone.class);

        List<Predicate> predicates = params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                .map(entry -> createPredicate(criteriaBuilder, root,
                        entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        Optional.of(predicates)
                .filter(list -> !list.isEmpty())
                .ifPresent(list -> query.where(criteriaBuilder
                        .and(list.toArray(new Predicate[0]))));

        query.select(root);
        Session session = factory.openSession();
        try {
            return session.createQuery(query).getResultList();
        } finally {
            session.close();
        }
    }

    private Predicate createPredicate(CriteriaBuilder criteriaBuilder,
                                      Root<Phone> root, String key, String[] values) {
        return values.length == 1
                ? criteriaBuilder.equal(root.get(key), values[0])
                : createInPredicate(criteriaBuilder, root, key, values);
    }

    private Predicate createInPredicate(CriteriaBuilder criteriaBuilder,
                                        Root<Phone> root, String key, String[] values) {
        CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get(key));
        Arrays.stream(values).forEach(inClause::value);
        return inClause;
    }
}
