package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
        } catch (RuntimeException e) {
            throw new RuntimeException("Can not add phone to the database");
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
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);
            CriteriaBuilder.In<String> predicateColor = cb.in(root.get("color"));
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicateColor.value(entry.getKey());
            }

            CriteriaBuilder.In<String[]> predicateModel = cb.in(root.get("maker"));
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicateModel.value(entry.getValue());
            }

            criteriaQuery.where(cb.and(predicateColor, predicateModel));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
