package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone "
                    + phone, e);
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
            CriteriaBuilder.In<String> countryPredicate
                    = cb.in(phoneRoot.get("countryManufactured"));
            CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
            Predicate predicate = cb.conjunction();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String key = param.getKey();
                String[] values = param.getValue();
                Path<String> path = phoneRoot.get(key);

                CriteriaBuilder.In<String> inClause = cb.in(path);
                Arrays.stream(values).forEach(inClause::value);

                predicate = cb.and(predicate, inClause);
            }
            return session.createQuery(query.where(predicate)).getResultList();
        }
    }
}
