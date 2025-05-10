package ma.hibernate.dao;

import java.util.ArrayList;
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
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                throw new RuntimeException("Can't add a movie to database", e);
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        CriteriaBuilder cb = factory.getCriteriaBuilder();
        CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
        Root<Phone> phoneRoot = query.from(Phone.class);
        List<CriteriaBuilder.In<String>> predicates = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(entry.getKey()));
            for (String value : entry.getValue()) {
                predicate.value(value);
            }
            predicates.add(predicate);
        }
        query.where(cb.and(predicates.toArray(Predicate[]::new)));
        try (Session session = factory.openSession()) {
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones by criteria.", e);
        }
    }
}
