package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save a phone to DB.", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {

        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(params.get("countryManufactured"))
                    .filter(values -> values.length > 0)
                    .ifPresent(values -> {
                        CriteriaBuilder.In<String> countryManufactured = cb.in(phoneRoot
                                .get("countryManufactured"));
                        Arrays.stream(values).forEach(countryManufactured::value);
                        predicates.add(countryManufactured);
                    });

            Optional.ofNullable(params.get("maker"))
                    .filter(values -> values.length > 0)
                    .ifPresent(values -> {
                        CriteriaBuilder.In<String> maker = cb.in(phoneRoot.get("maker"));
                        Arrays.stream(values).forEach(maker::value);
                        predicates.add(maker);
                    });

            Optional.ofNullable(params.get("color"))
                    .filter(values -> values.length > 0)
                    .ifPresent(values -> {
                        CriteriaBuilder.In<String> color = cb.in(phoneRoot.get("color"));
                        Arrays.stream(values).forEach(color::value);
                        predicates.add(color);
                    });

            Optional.ofNullable(params.get("model"))
                    .filter(values -> values.length > 0)
                    .ifPresent(values -> {
                        CriteriaBuilder.In<String> model = cb.in(phoneRoot.get("model"));
                        Arrays.stream(values).forEach(model::value);
                        predicates.add(model);
                    });

            Optional.of(predicates)
                    .filter(p -> !p.isEmpty())
                    .ifPresent(p -> query.where(p.toArray(new Predicate[0])));

            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones by parameters.", e);
        }
    }
}
