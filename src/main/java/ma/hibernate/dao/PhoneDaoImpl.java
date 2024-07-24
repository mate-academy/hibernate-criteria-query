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
            throw new RuntimeException("Cannot save phone: " + phone.toString() + "to DB", e);
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

            CriteriaBuilder.In<Object> countryManufacturedPredic =
                    cb.in(root.get("countryManufactured"));
            for (String country : params.get("countryManufactured")) {
                countryManufacturedPredic.value(country);
            }

            CriteriaBuilder.In<Object> makerPredic = cb.in(root.get("maker"));
            for (String maker : params.get("maker")) {
                makerPredic.value(maker);
            }

            CriteriaBuilder.In<Object> colorPredic = cb.in(root.get("color"));
            for (String color : params.get("color")) {
                colorPredic.value(color);
            }

            query.where(cb.and(countryManufacturedPredic, makerPredic, colorPredic));
            return session.createQuery(query).getResultList();
        }
    }
}
