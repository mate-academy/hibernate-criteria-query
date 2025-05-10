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
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to save the phone number: " + phone
                            + ". Error: " + e
            );
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String key = param.getKey();
                CriteriaBuilder.In<String> predicate = cb.in(root.get(key));
                for (String v : param.getValue()) {
                    predicate.value(v);
                }
                predicates.add(predicate);
            }
            query.where(
                    cb.and(!predicates.isEmpty() ? predicates.toArray(Predicate[]::new) : null)
            );
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch Phones"
            );
        }
    }
}
