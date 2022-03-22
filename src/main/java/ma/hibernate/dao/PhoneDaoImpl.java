package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.HibernateException;
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
        } catch (HibernateException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone in DB: " + phone, ex);
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
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);
            Predicate[] predicatesArray = new Predicate[params.size()];
            CriteriaBuilder.In<String> predicate;
            int count = 0;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicate = cb.in(root.get(entry.getKey()));
                for (String param : entry.getValue()) {
                    predicate.value(param);
                }
                predicatesArray[count] = predicate;
                count++;
            }
            cq.where(cb.and(predicatesArray));
            return session.createQuery(cq).getResultList();
        } catch (HibernateException ex) {
            throw new RuntimeException("Can't find all parameters: " 
                                       + params, ex);
        }
    }
}
