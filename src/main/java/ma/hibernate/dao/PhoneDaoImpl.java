package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Creation failure! Params: " + phone, ex);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            Predicate[] predicates = new Predicate[params.size()];
            int index = 0;
            for (Map.Entry<String, String[]> criteria : params.entrySet()) {
                String key = criteria.getKey();
                String[] values = criteria.getValue();
                CriteriaBuilder.In<String> predicate = criteriaBuilder.in(phoneRoot.get(key));
                Arrays.stream(values).forEach(predicate::value);
                predicates[index] = predicate;
                index++;
            }
            Predicate predicate = criteriaBuilder.and(predicates);
            criteriaQuery.where(predicate);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to find all phones!", ex);
        }
    }
}
