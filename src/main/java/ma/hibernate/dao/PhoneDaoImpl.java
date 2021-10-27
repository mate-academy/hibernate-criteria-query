package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    private static final int FIRST_INPUT_PARAMETR_POSITION = 0;
    private static final int SECOND_INPUT_PARAMETR_POSITION = 1;
    private static final int THIRD_INPUT_PARAMETR_POSITION = 2;

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
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone" + phone, e);
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
            if (params.size() == 0) {
                return session.createQuery(query).getResultList();
            }
            List<Predicate> predicates = getPredicates(params, cb, phoneRoot);
            Predicate predicateResult = getPredicate(cb, predicates);
            query = query.where(predicateResult);
            return session.createQuery(query).getResultList();
        }
    }

    private List<Predicate> getPredicates(Map<String, String[]> params, CriteriaBuilder cb,
                                          Root<Phone> phoneRoot) {
        CriteriaBuilder.In<String> predicate;
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, String[]> el : params.entrySet()) {
            predicate = cb.in(phoneRoot.get(el.getKey()));
            if (el.getValue() != null) {
                for (String e : el.getValue()) {
                    predicate.value(e);
                }
                predicates.add(predicate);
            }
        }
        return predicates;
    }

    private Predicate getPredicate(CriteriaBuilder cb, List<Predicate> predicates) {
        return predicates.size() == 1 ? cb.and(predicates.get(FIRST_INPUT_PARAMETR_POSITION))
                : predicates.size() == 2 ? cb.and(predicates.get(FIRST_INPUT_PARAMETR_POSITION),
                predicates.get(SECOND_INPUT_PARAMETR_POSITION))
                : cb.and(predicates.get(FIRST_INPUT_PARAMETR_POSITION),
                predicates.get(SECOND_INPUT_PARAMETR_POSITION),
                predicates.get(THIRD_INPUT_PARAMETR_POSITION));
    }
}

