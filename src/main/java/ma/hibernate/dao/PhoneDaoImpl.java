package ma.hibernate.dao;

import jakarta.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;

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

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert to DB phone: " + phone, e);
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
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            JpaRoot<Phone> phoneRoot = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();

                try {
                    Field field = Phone.class.getDeclaredField(key);
                    field.setAccessible(true);

                    List<Predicate> subPredicates = new ArrayList<>();
                    for (String value : values) {
                        subPredicates.add(cb.equal(phoneRoot.get(key), value));
                    }
                    predicates.add(cb.or(subPredicates.toArray(new Predicate[0])));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("Field not exist");
                }
            }
            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        }
    }
}
