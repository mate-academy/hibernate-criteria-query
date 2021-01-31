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
            throw new RuntimeException("Can't create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> parameters) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            List<Predicate> andPredicateList = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String[] stringArray = entry.getValue();
                List<Predicate> orPredicateList = new ArrayList<>();
                for (String parameter : stringArray) {
                    Predicate orPredicate =
                            criteriaBuilder.equal(phoneRoot.get(entry.getKey()), parameter);
                    orPredicateList.add(orPredicate);
                }
                Predicate andPredicate =
                        criteriaBuilder.or(orPredicateList.toArray(new Predicate[]{}));
                andPredicateList.add(andPredicate);
            }
            query.select(phoneRoot).where(andPredicateList.toArray(new Predicate[]{}));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong. "
                    + "Can`t find phones with parameters " + parameters, e);
        }
    }
}
