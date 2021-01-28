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
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneCriteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneCriteriaQuery.from(Phone.class);
            Predicate andPredicate = criteriaBuilder.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                andPredicate = criteriaBuilder
                        .and(andPredicate, phoneRoot.get(entry.getKey()).in(entry.getValue()));
            }
            phoneCriteriaQuery.select(phoneRoot).where(andPredicate);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phone for params " + params, e);
        }
    }
}
