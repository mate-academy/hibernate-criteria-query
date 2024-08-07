package ma.hibernate.dao;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone: " + phone, e);
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
            CriteriaBuilder criBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            List<CriteriaBuilder.In<String>> predicates = new ArrayList<>();

            params.forEach((k, v) -> {
                predicates.add(criBuilder.in(phoneRoot.get(k)));
                Arrays.stream(v).forEach(predicates.getLast()::value);
            });
            query.where(criBuilder.and(predicates.toArray(Predicate[]::new)));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            String mapToString = params.entrySet().stream()
                    .map(entry -> entry.getKey() + " = " + Arrays.toString(entry.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));
            throw new EntityNotFoundException("Can't find any phones by those options: "
                    + mapToString);
        }
    }
}
