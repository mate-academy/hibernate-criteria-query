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
            throw new RuntimeException("Can`t write phone: " + phone + " to DB.", e);
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
            CriteriaQuery<Phone> findAllPhonesQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllPhonesQuery.from(Phone.class);
            Predicate combinedPredicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> currentPredicate =
                        cb.in(phoneRoot.get(entry.getKey()));
                for (String parameterValue : entry.getValue()) {
                    currentPredicate.value(parameterValue);
                }
                combinedPredicate = cb.and(combinedPredicate, currentPredicate);
            }
            return session.createQuery(findAllPhonesQuery.where(combinedPredicate)).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can`t fetch data from DB.", e);
        }
    }
}
