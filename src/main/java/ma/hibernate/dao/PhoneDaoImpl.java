package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
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
import org.hibernate.query.criteria.HibernateCriteriaBuilder;

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
            return phone;
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to create phone: " + phone, exception);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>(params.size());
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                Path<String> path = root.get(entry.getKey());
                CriteriaBuilder.In<String> predicate = criteriaBuilder.in(path);
                Arrays.stream(entry.getValue()).forEach(predicate::value);
                predicates.add(predicate);
            }
            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to find phones with parameters: " + params, exception);
        }
    }
}
