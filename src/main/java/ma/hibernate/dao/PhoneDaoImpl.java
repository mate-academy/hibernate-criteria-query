package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.getCurrentSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone: " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.getCurrentSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            CriteriaBuilder.In<String> countryManufacturedPredicate =
                    cb.in(phoneRoot.get("countryManufactured"));
            for (String country : params.get("countryManufactured")) {
                countryManufacturedPredicate.value(country);
            }
            CriteriaBuilder.In<String> producersPredicate = cb.in(phoneRoot.get("producer"));
            for (String producer : params.get("producer")) {
                producersPredicate.value(producer);
            }
            CriteriaBuilder.In<String> colorsPredicate = cb.in(phoneRoot.get("color"));
            for (String color : params.get("color")) {
                colorsPredicate.value(color);
            }

            for (String key : params.keySet()) {
                if (key.equals("countryManufactured")) {
                    params.get(key)
                }
            }
            query.where(cb.and(countryManufacturedPredicate, producersPredicate, colorsPredicate));
            return session.createQuery(query).getResultList();

        }
    }
}
