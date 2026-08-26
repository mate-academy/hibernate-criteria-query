package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        factory.inTransaction(session -> {
            try {
                session.persist(phone);
            } catch (RuntimeException e) {
                throw new RuntimeException("Can't create phone: " + phone, e);
            }
        });
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();
            for (var entry : params.entrySet()) {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(
                        phoneRoot.get(entry.getKey())
                );
                Arrays.stream(entry.getValue()).forEach(inClause::value);
                predicates.add(inClause);
            }
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't filter phones", e);
        }
    }
}
