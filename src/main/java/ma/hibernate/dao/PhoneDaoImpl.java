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
            throw new RuntimeException("Cannot save phone: " + phone.toString() + "to DB", e);
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
            Root<Phone> root = query.from(Phone.class);

            Predicate summPredic = cb.conjunction();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();

                Predicate keyValuePredic = cb.disjunction();

                for (String value : entry.getValue()) {
                    keyValuePredic = cb.or(keyValuePredic, cb.equal(root.get(key), value));
                }
                summPredic = cb.and(summPredic, keyValuePredic);
            }

            return session.createQuery(query.where(summPredic)).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get all records by parameters: "
                    + params.toString(), e);
        }
    }
}
