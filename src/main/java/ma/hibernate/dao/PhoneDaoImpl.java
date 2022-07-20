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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone to DB, Phone: " + phone, e);
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
            Root<Phone> from = query.from(Phone.class);
            Predicate and = criteriaBuilder.and();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(from.get(param.getKey()));
                for (String type : param.getValue()) {
                    in.value(type);
                }
                and = criteriaBuilder.and(and, in);
            }
            query.where(and);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phone by criteria: " + params, e);
        }

    }
}
