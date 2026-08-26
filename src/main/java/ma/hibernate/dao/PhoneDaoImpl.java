package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    private CriteriaBuilder.In<String> predicate;
    private Predicate finalPredicate;
    private Session session;
    private Transaction transaction;

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Phone must not be null");
        }
        if (phone.getModel() == null || phone.getModel().isEmpty()) {
            throw new IllegalArgumentException("Phone model must not be null or empty");
        }
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
            throw new RuntimeException("Could not add phone: " + phone, e);
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
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            finalPredicate = criteriaBuilder.conjunction();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                predicate = criteriaBuilder.in(root.get(key));
                for (String value : values) {
                    predicate.value(value);
                }
                finalPredicate = criteriaBuilder.and(predicate, finalPredicate);
            }
            return session.createQuery(query.where(criteriaBuilder.and(finalPredicate))).list();
        } catch (Exception e) {
            throw new RuntimeException("Could not find all phones", e);
        }
    }
}
