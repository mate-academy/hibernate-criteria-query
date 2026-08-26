package ma.hibernate.dao;

import java.util.Arrays;
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
            throw new RuntimeException("Can't save phone " + phone + " to DB ", e);
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
            Root<Phone> phoneRoot = query.from(Phone.class);
            CriteriaBuilder.In<Object> predicate = null;
            Predicate resultPredicate = null;
            for (Map.Entry<String,String[]> pair : params.entrySet()) {
                predicate = cb.in(phoneRoot.get(pair.getKey()));
                Arrays.stream(pair.getValue()).forEach(predicate::value);
                Predicate recursivPredicate = (resultPredicate != null)
                        ? resultPredicate : predicate;
                resultPredicate = cb.and(predicate,recursivPredicate);
            }
            CriteriaQuery<Phone> phoneCriteriaQuery = (resultPredicate == null)
                    ? query : query.where(resultPredicate);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        }
    }
}
