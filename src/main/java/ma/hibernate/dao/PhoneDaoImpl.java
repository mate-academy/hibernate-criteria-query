package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
            throw new RuntimeException("Can't create a phone: " + phone, e);
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
            // where key in 'value1, value2, value3...'
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String param = entry.getKey();
                String[] values = entry.getValue();
                CriteriaBuilder.In<String> paramPredicate = cb.in(phoneRoot.get(param));
                for (String value : values) {
                    paramPredicate.value(value);
//                    query.where(cb.equal(phoneRoot.get(entry.getKey()), value));
                }
                cb.and(paramPredicate);
            }
            List<Phone> resultList = session.createQuery(query).getResultList();
            return resultList;
        } catch (Exception e) {
            throw new RuntimeException("Can't find all by parameters: " + params, e);
        }
    }
}
