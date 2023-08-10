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
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Couldn't save new phone " + phone, e);
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate[] predicates = new Predicate[params.size()];
            int index = 0;
            for (Map.Entry<String, String[]> input : params.entrySet()) {
                String column = input.getKey();
                predicates[index] = cb.in(root.get(column));
                String[] criteria = input.getValue();
                CriteriaBuilder.In<String> predicate = cb.in(root.get(column));
                for (String criterion : criteria) {
                    predicate.value(criterion);
                }
                predicates[index++] = predicate;
            }
            query.where(predicates);
            return session.createQuery(query).getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("there is no phone with this params: " + params, e);
        }
    }
}
