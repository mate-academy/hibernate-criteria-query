package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create a phone: " + phone, e);
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

            List<Predicate> predicates = new ArrayList<Predicate>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                if (entry.getKey().equals("model")) {
                    CriteriaBuilder.In<Object> modelPredicate = cb.in(phoneRoot.get("model"));
                    for (String model : entry.getValue()) {
                        modelPredicate.value(model);
                    }
                    predicates.add(modelPredicate);
                    continue;
                }
                if (entry.getKey().equals("maker")) {
                    CriteriaBuilder.In<Object> makerPredicate = cb.in(phoneRoot.get("maker"));
                    for (String maker : entry.getValue()) {
                        makerPredicate.value(maker);
                    }
                    predicates.add(makerPredicate);
                    continue;
                }
                if (entry.getKey().equals("color")) {
                    CriteriaBuilder.In<Object> colorPredicate = cb.in(phoneRoot.get("color"));
                    for (String color : entry.getValue()) {
                        colorPredicate.value(color);
                    }
                    predicates.add(colorPredicate);
                    continue;
                }
                if (entry.getKey().equals("os")) {
                    CriteriaBuilder.In<Object> osPredicate = cb.in(phoneRoot.get("os"));
                    for (String os : entry.getValue()) {
                        osPredicate.value(os);
                    }
                    predicates.add(osPredicate);
                    continue;
                }
                if (entry.getKey().equals("countryManufactured")) {
                    CriteriaBuilder.In<Object> countryManufacturedPredicate = cb
                            .in(phoneRoot.get("countryManufactured"));
                    for (String countryManufactured : entry.getValue()) {
                        countryManufacturedPredicate.value(countryManufactured);
                    }
                    predicates.add(countryManufacturedPredicate);
                }
            }
            if (predicates.size() > 0) {
                query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            }
            return session.createQuery(query).getResultList();
        }
    }
}
