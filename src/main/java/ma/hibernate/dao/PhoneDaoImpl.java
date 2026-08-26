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
            assert transaction != null;
            transaction.rollback();
            throw new RuntimeException("Can't insert phone: " + phone, e);
        } finally {
            assert session != null;
            session.close();
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
                    .filter(arr -> arr.length > 0)
                    .ifPresent(arr -> {
                        CriteriaBuilder.In<String> countryPg =
                                cb.in(phoneRoot.get("countryManufactured"));
                        Arrays.stream(arr).forEach(countryPg::value);
                        predicates.add(countryPg);
                    });

            Optional.ofNullable(params.get("maker"))
                    .filter(arr -> arr.length > 0)
                    .ifPresent(arr -> {
                        CriteriaBuilder.In<Object> makerPg = cb.in((phoneRoot.get("maker")));
                        Arrays.stream(arr).forEach(makerPg::value);
                        predicates.add(makerPg);
                    });

            Optional.ofNullable(params.get("color"))
                    .filter(arr -> arr.length > 0)
                    .ifPresent(arr -> {
                        CriteriaBuilder.In<String> colorPg = cb.in(phoneRoot.get("color"));
                        Arrays.stream(arr).forEach(colorPg::value);
                        predicates.add(colorPg);
                    });

            Optional.ofNullable(params.get("model"))
                    .filter(arr -> arr.length > 0)
                    .ifPresent(arr -> {
                        CriteriaBuilder.In<String> modelPg = cb.in(phoneRoot.get("model"));
                        Arrays.stream(arr).forEach(modelPg::value);
                        predicates.add(modelPg);
                    });

            query.where(cb.and(predicates.toArray(new Predicate[0])));

            return session.createQuery(query).getResultList();
        }
    }
}
