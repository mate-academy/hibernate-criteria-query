package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
        Transaction transaction = null;
        Session session = null;
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
            throw new RuntimeException("Can`t create phone " + phone, e);
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
            CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
            CriteriaBuilder.In<String> countryManufacturedPredicate
                    = cb.in(phoneRoot.get("countryManufactured"));
            CriteriaBuilder.In<String> modelPredicate = cb.in(phoneRoot.get("model"));
            for (Map.Entry<String, String[]> items : params.entrySet()) {
                if (items.getKey().equals("maker")) {
                    for (String value : items.getValue()) {
                        makerPredicate.value(value);
                    }
                } else if (items.getKey().equals("color")) {
                    for (String value : items.getValue()) {
                        colorPredicate.value(value);
                    }
                } else if (items.getKey().equals("countryManufactured")) {
                    for (String value : items.getValue()) {
                        countryManufacturedPredicate.value(value);
                    }
                } else {
                    for (String value : items.getValue()) {
                        modelPredicate.value(value);
                    }
                }
            }
            if (params.containsKey("maker") && params.containsKey("color")
                    && params.containsKey("countryManufactured")) {
                query.where(cb.and(colorPredicate, makerPredicate,
                        countryManufacturedPredicate));
            } else if (params.containsKey("maker") && params.containsKey("color")) {
                query.where(cb.and(makerPredicate, colorPredicate));
            } else if (params.containsKey("maker")) {
                query.where(cb.and(makerPredicate));
            } else if (params.containsKey("model")) {
                query.where(cb.and(modelPredicate));
            }
            return session.createQuery(query).getResultList();
        }
    }
}
