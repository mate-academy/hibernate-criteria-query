package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.exception.DataProcessingException;
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
            throw new DataProcessingException("Can't insert phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate predicate = builder.and(getPredicates(root, params));
            query.select(root).where(predicate);
            return session.createQuery(query).getResultList();
        }
    }

    private Predicate[] getPredicates(Root<Phone> root, Map<String, String[]> params) {
        Predicate[] predicates = new Predicate[params.size()];
        int i = 0;
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            predicates[i++] = root.get(entry.getKey()).in(entry.getValue());
        }
        return predicates;
    }
}
