package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.Predicate;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaInPredicate;
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
        try (Session session = factory.openSession()) {
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            JpaRoot<Phone> phoneRoot = query.from(Phone.class);

            List<JpaInPredicate> predicates = new ArrayList<>();

            String[] countriesManufactured = params.get("countryManufactured");
            if (countriesManufactured != null) {
                JpaInPredicate<String> countryManufacturedPredicate = cb
                        .in(phoneRoot.get("countryManufactured"));
                for (String country : countriesManufactured) {
                    countryManufacturedPredicate.value(country);
                }
                predicates.add(countryManufacturedPredicate);
            }

            String[] makers = params.get("maker");
            if (makers != null) {
                JpaInPredicate<String> makerPredicate = cb.in(phoneRoot.get("maker"));
                for (String maker : makers) {
                    makerPredicate.value(maker);
                }
                predicates.add(makerPredicate);
            }

            String[] colors = params.get("color");
            if (colors != null) {
                JpaInPredicate<String> colorPredicate = cb.in(phoneRoot.get("color"));
                for (String color : colors) {
                    colorPredicate.value(color);
                }
                predicates.add(colorPredicate);
            }

            String[] models = params.get("model");
            if (models != null) {
                JpaInPredicate<String> modelPredicate = cb.in(phoneRoot.get("model"));
                for (String model : models) {
                    modelPredicate.value(model);
                }
                predicates.add(modelPredicate);
            }

            if (!predicates.isEmpty()) {
                query.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all phones", e);
        }
    }
}
