package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {

    private final SessionFactory sessionFactory;

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Phone create(Phone phone) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create Phone on DB", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> productRoot = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            Map<String, Function<String[], Predicate>> predicateMap = new HashMap<>();
            predicateMap.put("model", values -> productRoot.get("model").in((Object[]) values));
            predicateMap.put("color", values -> productRoot.get("color").in((Object[]) values));
            predicateMap.put("maker", values -> productRoot.get("maker").in((Object[]) values));
            predicateMap.put("os", values -> productRoot.get("os").in((Object[]) values));
            predicateMap.put("countryManufactured", values -> productRoot
                    .get("countryManufactured").in((Object[]) values));
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                Function<String[], Predicate> predicateFunction = predicateMap.get(entry.getKey());
                predicates.add(predicateFunction.apply(entry.getValue()));
            }
            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding phones", e);
        }
    }
}
