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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate predicateAnd = criteriaBuilder.and();
            for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
                CriteriaBuilder.In<String> param =
                        criteriaBuilder.in(phoneRoot.get(stringEntry.getKey()));
                for (String temp : stringEntry.getValue()) {
                    param.value(temp);
                }
                predicateAnd = criteriaBuilder.and(param, predicateAnd);
            }
            return session.createQuery(query.where(predicateAnd)).list();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phone with param " + params + ". ", e);
        }
    }
}
