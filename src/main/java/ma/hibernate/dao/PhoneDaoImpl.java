package ma.hibernate.dao;

import java.util.ArrayList;
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
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException ("Phone " + phone + "wasn't created", e);
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
            CriteriaQuery<Phone> phoneQuery = cb.createQuery(Phone.class);
            Root<Phone> root = phoneQuery.from(Phone.class);
            List<Predicate> andPredicateList = new ArrayList<>();
            for (String key : params.keySet()) {
                List<Predicate> orPredicateList = new ArrayList<>();
                for (String val : params.get(key)) {
                    orPredicateList.add(cb.equal(root.get(key), val));
                }
                andPredicateList.add(cb.or(orPredicateList.toArray(new Predicate[0])));
            }
            phoneQuery.select(root).where(andPredicateList.toArray(new Predicate[0]));
            return session.createQuery(phoneQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException ("Can't get list of all phones");
        }
    }
}
