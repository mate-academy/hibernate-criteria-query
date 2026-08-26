package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ma.hibernate.exception.DataProcessingException;
import ma.hibernate.model.Phone;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        return executeInTransaction(session -> {
            session.persist(phone);
            return phone;
        });
    }

    private <T> T executeInTransaction(Function<Session, T> action) {
        Transaction transaction = null;
        try (Session session = super.factory.openSession()) {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (HibernateException e) {
            rollbackTransaction(transaction);
            throw new DataProcessingException("Transaction failed", e);
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        Optional.ofNullable(transaction).ifPresent(Transaction::rollback);
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = super.factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phone = query.from(Phone.class);

            List<Predicate> predicates = Stream.of(
                            createPredicate(cb, phone, "countryManufactured",
                                    params.get("countryManufactured"),
                                    values -> phone.get("countryManufactured")
                                            .in((Object[]) values)),
                            createPredicate(cb, phone, "maker", params.get("maker"),
                                    values -> phone.get("maker").in((Object[]) values)),
                            createPredicate(cb, phone, "color", params.get("color"),
                                    values -> phone.get("color").in((Object[]) values)),
                            createPredicate(cb, phone, "model", params.get("model"),
                                    values -> phone.get("model").in((Object[]) values))
                    )
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            query.select(phone).where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        } catch (HibernateException e) {
            throw new DataProcessingException("Failed to create session or execute query", e);
        }
    }

    private Predicate createPredicate(CriteriaBuilder cb, Root<Phone> phone,
                                      String key, String[] values,
                                      Function<String[], Predicate> createFunction) {
        return Optional.ofNullable(values)
                .filter(val -> val.length > 0)
                .map(createFunction)
                .orElse(null);
    }
}
