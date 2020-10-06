package ma.hibernate.dao;

import java.util.LinkedList;
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
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can`t create phone entity ", ex);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> q = cb.createQuery(Phone.class);
            Root<Phone> root = q.from(Phone.class);
            List<Predicate> predicates = new LinkedList();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicates.add(root.get(entry.getKey()).in(entry.getValue()));
            }
            Predicate predicate = cb.and(predicates.toArray(new Predicate[0]));
            q.select(root).where(predicate);
            return session.createQuery(q).getResultList();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can`t find entities ", ex);
        } finally {
            session.close();
        }
    }
}
