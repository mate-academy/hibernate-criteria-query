package ma.hibernate.dao;

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
            throw new RuntimeException("Exception during creation of phone: " + phone, e);
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
            CriteriaQuery<Phone> findByParamsQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = findByParamsQuery.from(Phone.class);
            Predicate allPredicates = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> newInPredicate =
                        cb.in(phoneRoot.get(entry.getKey()));
                for (String data : entry.getValue()) {
                    newInPredicate.value(data);
                }
                allPredicates = cb.and(allPredicates, newInPredicate);
            }
            findByParamsQuery.where(allPredicates);
            return session.createQuery(findByParamsQuery).getResultList();
        } catch (Exception e) {
            System.out.println("Exception while trying to get phones from db "
                    + "by parameters:");
            params.forEach((key, value) -> System.out.println(key + " : " + value));
            throw new RuntimeException("Stack trace: ", e);
        }
    }
}
