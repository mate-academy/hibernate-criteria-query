package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
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
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone " + phone + " to DB", e);
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
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);

            In<String> inCountryPredicate = builder.in(root.get("countryManufactured"));
            for (String country : params.get("countryManufactured")) {
                inCountryPredicate.value(country);
            }

            In<String> inMakerPredicate = builder.in(root.get("maker"));
            for (String maker : params.get("maker")) {
                inMakerPredicate.value(maker);
            }

            In<String> inColorPredicate = builder.in(root.get("color"));
            for (String color : params.get("color")) {
                inColorPredicate.value(color);
            }

            Predicate andPredicate = builder.and(inCountryPredicate,
                    inMakerPredicate, inColorPredicate);
            query.where(andPredicate);
            return session.createQuery(query).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Can't get phones from DB", e);
        }
    }
}
