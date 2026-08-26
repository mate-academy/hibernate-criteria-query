package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
            throw new RuntimeException("Can't persist phone = " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            Function<Map.Entry<String, String[]>, Predicate> inFunction = (entry) -> {
                CriteriaBuilder.In<String> attributePredicate =
                        criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String parameter : entry.getValue()) {
                    attributePredicate.value(parameter);
                }
                return attributePredicate;
            };
            List<Predicate> inPredicates = params.entrySet().stream()
                    .map(inFunction)
                    .toList();
            Predicate finalPredicate = criteriaBuilder.and(inPredicates.toArray(Predicate[]::new));
            criteriaQuery.where(finalPredicate);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones by params = " + params, e);
        }
    }
}
