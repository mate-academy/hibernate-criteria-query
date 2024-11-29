package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            throw new RuntimeException("Can't insert phone " + phone, e);
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
            CriteriaQuery<Phone> criteria = builder.createQuery(Phone.class);
            Root<Phone> root = criteria.from(Phone.class);
            criteria.select(root);

            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry: params.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                Predicate predicate = root.get(key).in((Object[]) values);
                predicates.add(predicate);
            }
            criteria.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones by parameters", e);
        }
    }
}
