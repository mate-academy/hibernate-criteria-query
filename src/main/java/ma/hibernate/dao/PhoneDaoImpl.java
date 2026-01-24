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
            session = super.factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
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
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = null;

        String[] countryManufactured = params.get("countryManufactured");
        String[] makers = params.get("maker");
        String[] colors = params.get("color");
        try {
            session = super.factory.openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);

            CriteriaBuilder.In<String> countryPredicate = builder.in(
                    root.get("countryManufactured"));
            for (String country : countryManufactured) {
                countryPredicate.value(country);
            }

            CriteriaBuilder.In<String> makerPredicate = builder.in(root.get("maker"));
            for (String maker : makers) {
                makerPredicate.value(maker);
            }

            CriteriaBuilder.In<String> colorPredicate = builder.in(root.get("color"));
            for (String color : colors) {
                colorPredicate.value(color);
            }

            query.where(builder.and(countryPredicate, makerPredicate, colorPredicate));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find any phone.", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
