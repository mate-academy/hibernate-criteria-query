package ma.hibernate.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            CriteriaBuilder.In<String> countryPredicate = cb.in(phoneRoot.get("countryManufactured"));
            String[] countries = params.get("countryManufactured");
            for (String country : countries) {
                countryPredicate.value(country);
            }

            CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
            String[] makers = params.get("maker");
            for (String maker : makers) {
                makerPredicate.value(maker);
            }

            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
            String[] colors = params.get("color");
            for (String color : colors) {
                colorPredicate.value(color);
            }

            cb.and(colorPredicate, makerPredicate, colorPredicate);
            return session.createQuery(query).getResultList();
        }
    }
}
