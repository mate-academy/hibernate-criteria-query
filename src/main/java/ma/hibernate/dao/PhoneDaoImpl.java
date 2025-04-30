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
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;
        Session session = null;
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
            throw new RuntimeException("Can't insert producte " + phone, e);
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
            Root<Phone> productRoot = query.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();

            params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .forEach(entry -> {
                        String fieldName = entry.getKey();
                        String[] fieldValues = entry.getValue();
                        CriteriaBuilder.In<String> fieldPredicate = cb.in(productRoot
                                .get(fieldName));
                        Arrays.stream(fieldValues).forEach(fieldPredicate::value);
                        predicates.add(fieldPredicate);
                    });

            Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[0]));

            query.where(finalPredicate);

            return session.createQuery(query.select(productRoot)).getResultList();
        }
    }
}
