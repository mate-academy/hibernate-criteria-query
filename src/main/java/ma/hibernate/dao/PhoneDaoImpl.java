package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
            throw new RuntimeException("Can't insert Phone entity: " + phone, e);
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
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = cq.from(Phone.class);

            Stream<Predicate> dynamicPredicates = params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .map(entry -> {
                        String paramName = entry.getKey();
                        String[] values = entry.getValue();

                        CriteriaBuilder.In<String> inClause = cb.in(phoneRoot.get(paramName));

                        Arrays.stream(values).forEach(inClause::value);

                        return inClause;
                    });

            Predicate finalPredicate = cb.and(
                    Stream.concat(
                            dynamicPredicates,
                            Stream.of(cb.isTrue(cb.literal(true)))
                    ).toArray(Predicate[]::new)
            );

            cq.where(finalPredicate);

            return session.createQuery(cq).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Can't find phones by specified criteria: " + params, e);
        }
    }
}
