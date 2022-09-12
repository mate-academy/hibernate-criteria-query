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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create " + phone + " in DB", e);
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
            Root<Phone> root = query.from(Phone.class);
            Predicate predicates = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> predicate = cb.in(root.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    predicate.value(value);
                }
                predicates = cb.and(predicates, predicate);
            }
            query.where(predicates);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder("Can't get products with parameters:");
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                stringBuilder.append(System.lineSeparator())
                        .append(entry.getKey()).append(": ")
                        .append(Arrays.toString(entry.getValue()));
            }
            throw new RuntimeException(stringBuilder.toString(), e);
        }
    }
}
