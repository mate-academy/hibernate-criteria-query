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
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save phone " + phone
                    + " to DB.", e);
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
            List<Predicate> criteriaList = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> currentPredicate = cb.in(phoneRoot.get(entry.getKey()));
                for (String parameter : entry.getValue()) {
                    currentPredicate.value(parameter);
                }
                criteriaList.add(currentPredicate);
            }
            Predicate allPredicates = cb.and(criteriaList.toArray(Predicate[]::new));
            return session.createQuery(findAllPhonesQuery.where(allPredicates)).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones with parameters " + params
                    + " from DB.", e);
        }
    }
}
