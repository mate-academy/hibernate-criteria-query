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
            CriteriaQuery<Phone> queryPhone = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> rootPhone = queryPhone.from(Phone.class);
            Predicate predicate = criteriaBuilder.conjunction();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String keys = entry.getKey();
                CriteriaBuilder.In<String> predicateKey = criteriaBuilder
                        .in(rootPhone.get(keys));
                for (String values : entry.getValue()) {
                    predicateKey.value(values);
                }
                predicate = criteriaBuilder.and(predicate, predicateKey);
            }
            queryPhone.where(predicate);
            return session.createQuery(queryPhone).getResultList();
        }
    }
}
