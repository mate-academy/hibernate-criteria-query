package ma.hibernate.dao;

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
            throw new RuntimeException("Can`t save phone " + phone + " to DB", e);
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
            CriteriaBuilder.In<String> countryManufactured =
                    cb.in(phoneRoot.get("countryManufactured"));
            CriteriaBuilder.In<String> maker = cb.in(phoneRoot.get("maker"));
            CriteriaBuilder.In<String> color = cb.in(phoneRoot.get("color"));
            CriteriaBuilder.In<String> model = cb.in(phoneRoot.get("model"));
            CriteriaBuilder.In<String> os = cb.in(phoneRoot.get("os"));
            Predicate predicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                if (entry.getKey().equals("countryManufactured")) {
                    for (String country : entry.getValue()) {
                        countryManufactured.value(country);
                    }
                    predicate = cb.and(predicate, countryManufactured);
                }
                if (entry.getKey().equals("maker")) {
                    for (String eachMaker : entry.getValue()) {
                        maker.value(eachMaker);
                    }
                    predicate = cb.and(predicate, maker);
                }
                if (entry.getKey().equals("color")) {
                    for (String eachColor : entry.getValue()) {
                        color.value(eachColor);
                    }
                    predicate = cb.and(predicate, color);
                }
                if (entry.getKey().equals("model")) {
                    for (String eachModel : entry.getValue()) {
                        model.value(eachModel);
                    }
                    predicate = cb.and(predicate, model);
                }
                if (entry.getKey().equals("os")) {
                    for (String eachOs : entry.getValue()) {
                        os.value(eachOs);
                    }
                    predicate = cb.and(predicate, os);
                }
            }
            query.where(predicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can`t find phones in DB", e);
        }
    }
}
