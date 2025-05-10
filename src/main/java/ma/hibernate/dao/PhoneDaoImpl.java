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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Cant save actor to DB. Actor: " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession();) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            if (params.isEmpty()) {
                return session.createQuery(query).getResultList();
            }
            Predicate resultPredicate = null;
            if (params.containsKey("maker")) {
                CriteriaBuilder.In<String> makerPredicate = cb.in(root.get("maker"));
                for (String maker : params.get("maker")) {
                    makerPredicate.value(maker);
                }
                resultPredicate = ((resultPredicate != null)
                        ? cb.and(makerPredicate, resultPredicate)
                        : makerPredicate);
            }
            if (params.containsKey("countryManufactured")) {
                CriteriaBuilder.In<String> countryManufactured =
                        cb.in(root.get("countryManufactured"));
                for (String manufactureCountryPredicate : params.get("countryManufactured")) {
                    countryManufactured.value(manufactureCountryPredicate);
                }
                resultPredicate = ((resultPredicate != null)
                        ? cb.and(countryManufactured, resultPredicate)
                        : countryManufactured);
            }
            if (params.containsKey("color")) {
                CriteriaBuilder.In<String> colorPredicate = cb.in(root.get("color"));
                for (String color : params.get("color")) {
                    colorPredicate.value(color);
                }
                resultPredicate = ((resultPredicate != null)
                        ? cb.and(colorPredicate, resultPredicate)
                        : colorPredicate);
            }
            if (params.containsKey("model")) {
                CriteriaBuilder.In<String> modelPredicate = cb.in(root.get("model"));
                for (String model : params.get("model")) {
                    modelPredicate.value(model);
                }
                resultPredicate = ((resultPredicate != null)
                        ? cb.and(modelPredicate, resultPredicate)
                        : modelPredicate);
            }
            query.where(resultPredicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cant: " + params, e);
        }
    }
}
