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
            throw new RuntimeException("Can't add phone to DB", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        String[] makers = params.get("maker");
        String[] colors = params.get("color");
        String[] countryManufactureds = params.get("countryManufactured");
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            CriteriaBuilder.In<String> makerPredicate = cb
                    .in(phoneRoot.get("maker"));
            for (String maker : makers) {
                makerPredicate.value(maker);
            }
            CriteriaBuilder.In<String> colorPredicate = cb
                    .in(phoneRoot.get("color"));
            for (String color : colors) {
                makerPredicate.value(color);
            }
            CriteriaBuilder.In<String> countryManufacturedPredicate = cb
                    .in(phoneRoot.get("countryManufactured"));
            for (String countryManufactured : countryManufactureds) {
                makerPredicate.value(countryManufactured);
            }

            query.where(cb.and(makerPredicate, colorPredicate, countryManufacturedPredicate));
            return session.createQuery(query).getResultList();
        }
    }
}
