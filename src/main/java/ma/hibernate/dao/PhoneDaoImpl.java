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
        Transaction transaction = null;
        Session session = null;
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
            throw new RuntimeException("Can't save phone to db " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = phoneQuery.from(Phone.class);
            List<Predicate> allAndPredicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entries : params.entrySet()) {
                List<Predicate> allOrPredicates = new ArrayList<>();
                String[] values = entries.getValue();
                for (String value : values) {
                    allOrPredicates.add(criteriaBuilder.equal(root.get(entries.getKey()), value));
                }
                allAndPredicates.add(criteriaBuilder.or(allOrPredicates
                        .toArray(new Predicate[]{})));
            }
            Predicate resultPredicate = criteriaBuilder.and(allAndPredicates
                    .toArray(new Predicate[]{}));
            phoneQuery.select(root).where(resultPredicate);
            return session.createQuery(phoneQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all phones by parameters ", e);
        }
    }
}
