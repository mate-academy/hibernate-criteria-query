package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
            session.persist(phone);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone to db: " + phone, e);
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
            Root<Phone> phoneRoot = query.from(Phone.class);

            List<Predicate> predicate = new ArrayList<>();

            if (params.containsKey("countryManufactured")) {
                predicate.add(phoneRoot.get("countryManufactured").in((Object[])
                        params.get("countryManufactured")));
            }

            if (params.containsKey("maker")) {
                predicate.add(phoneRoot.get("maker").in((Object[])
                        params.get("maker")));
            }

            if (params.containsKey("color")) {
                predicate.add(phoneRoot.get("color").in((Object[])
                        params.get("color")));
            }

            query.where(cb.and(predicate.toArray(new Predicate[0])));

            return session.createQuery(query).getResultList();
        }
    }
}
