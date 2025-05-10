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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone " + phone + " to Db ", e);
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
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate combinedPredicate = cb.and();
            for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
                CriteriaBuilder.In<String> partOfPredicate = cb.in(phoneRoot.get(stringEntry
                        .getKey()));
                for (String parameter : stringEntry.getValue()) {
                    partOfPredicate.value(parameter);
                }
                combinedPredicate = cb.and(combinedPredicate, partOfPredicate);
            }
            return session.createQuery(query.where(combinedPredicate)).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones ", e);
        }
    }
}
