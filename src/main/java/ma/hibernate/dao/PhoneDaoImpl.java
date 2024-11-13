package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
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
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can't create phone" + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = null;
        try {
            session = factory.openSession();
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            JpaRoot<Phone> phoneRoot = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();

                if (isNotNullOrEmpty(values)) {
                    CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(key));
                    for (String val : values) {
                        predicate.value(val);
                    }
                    predicates.add(predicate);
                }
            }

            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static boolean isNotNullOrEmpty(String[] colors) {
        return colors != null && colors.length != 0;
    }
}
