package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
            throw new RuntimeException("Can't create phone: " + phone, e);
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
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate finalPredicate = criteriaBuilder.and();
            for (Map.Entry<String, String[]> entrySet : params.entrySet()) {
                CriteriaBuilder.In<String> currentPredicate =
                        criteriaBuilder.in(root.get(entrySet.getKey()));
                for (String value : entrySet.getValue()) {
                    currentPredicate.value(value);
                }
                finalPredicate = criteriaBuilder.and(finalPredicate, currentPredicate);
            }
            query.where(finalPredicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones with params: " + params
                    .entrySet()
                    .stream()
                    .map(entry -> "key: " + entry.getKey() + ", value: " + entry.getValue())
                    .collect(Collectors.joining("; ")), e);
        }
    }
}
