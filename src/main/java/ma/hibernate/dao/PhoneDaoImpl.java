package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone " + phone
                    + " tp DB", e);
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
            CriteriaQuery<Phone> phonesQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phonesQuery.from(Phone.class);
            Predicate allConditions = criteriaBuilder.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> phonePredicate = criteriaBuilder
                        .in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    phonePredicate.value(value);
                }
                allConditions = criteriaBuilder.and(allConditions, phonePredicate);
            }
            phonesQuery.where(allConditions);
            return session.createQuery(phonesQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all phones from DB", e);
        }
    }
}
