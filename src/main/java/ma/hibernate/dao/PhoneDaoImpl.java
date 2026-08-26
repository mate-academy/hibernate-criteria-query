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
import org.hibernate.query.criteria.internal.predicate.InPredicate;

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
            throw new RuntimeException("Can't save "
                    + phone.toString() + "to DB", e);
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
            Root<Phone> rootEntry = query.from(Phone.class);
            List<Predicate> paramPredicateList = new ArrayList<Predicate>();

            for (String key:params.keySet()) {
                CriteriaBuilder.In<String> paramPredicate = cb.in(rootEntry.get(key));
                for (String maker : params.get(key)) {
                    paramPredicate.value(maker);
                }
                if (((InPredicate)paramPredicate).getValues().size() > 0) {
                    paramPredicateList.add(paramPredicate);
                }
            }

            if (paramPredicateList.size() > 0) {
                query.where(cb.and(paramPredicateList.toArray(new Predicate[0])));
            }

            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't fetch all Phone from DB", e);
        }
    }
}
