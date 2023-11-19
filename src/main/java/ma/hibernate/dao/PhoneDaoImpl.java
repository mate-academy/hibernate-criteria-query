package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
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
        try (Session session = factory.openSession()) {
            try {
                transaction = session.beginTransaction();
                session.persist(phone);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("can't add phone data: " + phone, e);
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query =cb.createQuery(Phone.class);
            Root<Phone> from = query.from(Phone.class);
            CriteriaBuilder.In<String> modelPredicate = cb.in(from.get("model"));
            CriteriaBuilder.In<String> makerPredicate = cb.in(from.get("maker"));
            CriteriaBuilder.In<String> colorPredicate = cb.in(from.get("color"));
            CriteriaBuilder.In<String> osPredicate = cb.in(from.get("os"));
            CriteriaBuilder.In<String> countyPredicate = cb.in(from.get("countryManufactured"));
            for (Map.Entry<String,String[]> entry : params.entrySet()) {
                if (entry.getKey().equals("model")) {
                    for (String model : entry.getValue()) {
                        modelPredicate.value(model);
                    }
                }
                if (entry.getKey().equals("maker")) {
                    for (String maker : entry.getValue()) {
                        makerPredicate.value(maker);
                    }
                }
                if (entry.getKey().equals("color")) {
                    for (String color : entry.getValue()) {
                        colorPredicate.value(color);
                    }
                }
                if (entry.getKey().equals("os")) {
                    for (String os : entry.getValue()){
                        osPredicate.value(os);
                    }
                }
                if (entry.getKey().equals("countryManufactured")) {
                    for (String contyManu : entry.getValue()) {
                        countyPredicate.value(contyManu);
                    }
                }
            }
            query.where(cb.or(modelPredicate, makerPredicate, colorPredicate, osPredicate, countyPredicate));
            return session.createQuery(query).getResultList();
        } catch (Exception e ) {
            throw new RuntimeException("can't find all in ");
        }
    }
}
