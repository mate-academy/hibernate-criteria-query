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
            throw new RuntimeException("Can't create Phone: " + phone, e);
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
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate resultPredicate = criteriaBuilder.and();
            for (Map.Entry<String, String[]> mapEntry : params.entrySet()) {
                CriteriaBuilder.In<Object> inPredicate =
                        criteriaBuilder.in(phoneRoot.get(mapEntry.getKey()));
                for (String filter : mapEntry.getValue()) {
                    inPredicate.value(filter);
                }
                resultPredicate = criteriaBuilder.and(resultPredicate, inPredicate);
            }
            return session.createQuery(query.where(resultPredicate)).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones", e);
        }
    }
}
