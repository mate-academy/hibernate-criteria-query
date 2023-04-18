package ma.hibernate.dao;

import ma.hibernate.exeption.DataProcessingException;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;

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
            throw new DataProcessingException("Can't create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = null;
        try {
            session = factory.openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);

            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate equal = null;
            CriteriaBuilder.In<String> in = null;
            for (Map.Entry<String, String[]> param : params.entrySet()) {

                equal = criteriaBuilder.equal(phoneRoot.get(param.getKey()), param.getKey());

                for (String val : param.getValue()) {
                    in = criteriaBuilder.in(phoneRoot.get(val));
                }
            }
            query.where((Predicate) query, in);

            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't findAll phone ", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
