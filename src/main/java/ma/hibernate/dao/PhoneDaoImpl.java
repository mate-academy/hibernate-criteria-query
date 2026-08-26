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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone '" + phone + "' to DB", e);
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
            CriteriaQuery<Phone> phoneCriteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneCriteriaQuery.from(Phone.class);
            Predicate finalPredicate = criteriaBuilder.conjunction();
            for (String field : params.keySet()) {
                CriteriaBuilder.In<String> predicateField
                        = criteriaBuilder.in(phoneRoot.get(field));
                for (String valueOfField : params.get(field)) {
                    predicateField.value(valueOfField);
                }
                finalPredicate = criteriaBuilder.and(finalPredicate, predicateField);
            }
            return session.createQuery(phoneCriteriaQuery
                    .where(finalPredicate)).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones by params '"
                    + params + "' from DB", e);
        }
    }
}
