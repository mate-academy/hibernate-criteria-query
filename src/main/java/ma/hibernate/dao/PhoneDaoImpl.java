package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Was not able to create a new phone.", e);
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
            CriteriaBuilder builder = factory.getCriteriaBuilder();
            CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate[] predicates = new Predicate[params.size()];
            int counter = 0;

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                In<String> currentPredicate = builder.in(phoneRoot.get(entry.getKey()));

                for (String value : entry.getValue()) {
                    currentPredicate.value(value);
                }
                predicates[counter] = currentPredicate;
                counter++;
            }
            query.where(builder.and(predicates));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Was not able to get a phone by parameters from the db", e);
        }
    }
}
