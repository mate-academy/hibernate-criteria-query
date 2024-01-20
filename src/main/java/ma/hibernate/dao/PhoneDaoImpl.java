package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
        Session session = null;
        Transaction transaction;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't add to DB Phone " + phone);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {

        try (Session session = factory.openSession()) {
            int index = 0;
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Predicate[]predicates = new Predicate[] {cb.conjunction(), cb.conjunction(),
                    cb.conjunction(),cb.conjunction(), cb.conjunction()};
            Root<Phone> phoneRoot = query.from(Phone.class);
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String []values = entry.getValue();
                CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(key));
                for (String value : values) {
                    predicate.value(value);
                }
                predicates[index] = predicate;
                index++;
            }
            query.where(cb.and(predicates[0],predicates[1],
                    predicates[2],predicates[3],predicates[4]));
            return session.createQuery(query).getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't get data from DB");
        }
    }
}
