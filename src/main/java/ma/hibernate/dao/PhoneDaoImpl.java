package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
            throw new RuntimeException("Can't create phone", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        List<Phone> list = null;
        Session session = factory.openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> q = cb.createQuery(Phone.class);
            Root<Phone> root = q.from(Phone.class);
            List<Predicate> andPredicate = new ArrayList<>();
            Predicate finalPredicate = null;
            for (String key : params.keySet()) {
                List<Predicate> orPredicate = new ArrayList<>();
                for (String value : params.get(key)) {
                    Path<Object> objectPath = root.get(key);
                    orPredicate.add(cb.equal(objectPath, value));
                }
                Predicate oneCriterionCombineOrPredicate =
                        cb.or(orPredicate.toArray(new Predicate[]{}));
                andPredicate.add(oneCriterionCombineOrPredicate);
            }
            finalPredicate = cb.and(andPredicate.toArray(new Predicate[]{}));
            Query<Phone> query = session.createQuery(q
                    .where(finalPredicate));
            list = query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones", e);
        }
        return list;
    }
}
