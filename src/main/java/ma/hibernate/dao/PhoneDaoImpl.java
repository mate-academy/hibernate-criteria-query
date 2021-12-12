package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
            throw new RuntimeException("Can't insert a phone " + phone
                    + "in a DB!", e);
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
            Set<Map.Entry<String, String[]>> entrySet = params.entrySet();

            CriteriaBuilder.In<Object> countryManufacturedPredicate = null;
            CriteriaBuilder.In<Object> makerPredicate = null;
            CriteriaBuilder.In<Object> colorPredicate = null;

            for (Map.Entry<String, String[]> parameter : entrySet) {
                if (Objects.equals(parameter.getKey(), "countryManufactured")) {
                    countryManufacturedPredicate = cb.in(phoneRoot
                            .get("countryManufactured"));
                    for (String country : parameter.getValue()) {
                        countryManufacturedPredicate.value(country);
                    }
                }
                if (Objects.equals(parameter.getKey(), "maker")) {
                    makerPredicate = cb.in(phoneRoot.get("maker"));
                    for (String maker : parameter.getValue()) {
                        makerPredicate.value(maker);
                    }
                }
                if (Objects.equals(parameter.getKey(), "color")) {
                    colorPredicate = cb.in(phoneRoot.get("color"));
                    for (String color : parameter.getValue()) {
                        colorPredicate.value(color);
                    }
                }
            }
            query.where(cb.and(countryManufacturedPredicate, makerPredicate, colorPredicate));
            return session.createQuery(query).getResultList();
        }
    }
}
