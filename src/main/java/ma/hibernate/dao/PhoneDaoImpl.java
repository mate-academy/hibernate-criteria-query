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
            throw new RuntimeException("Can't insert Phone " + phone, e);
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

            CriteriaBuilder.In<String> cmPredicate = null;
            if (params.containsKey("countryManufactured")
                    && params.get("countryManufactured") != null) {
                cmPredicate = cb.in(phoneRoot.get("countryManufactured"));
                for (String cm : params.get("countryManufactured")) {
                    cmPredicate.value(cm);
                }
            }

            CriteriaBuilder.In<String> makerPredicate = null;
            if (params.containsKey("maker") && params.get("maker") != null) {
                makerPredicate = cb.in(phoneRoot.get("maker"));
                for (String maker : params.get("maker")) {
                    makerPredicate.value(maker);
                }
            }

            CriteriaBuilder.In<String> colorPredicate = null;
            if (params.containsKey("color") && params.get("color") != null) {
                colorPredicate = cb.in(phoneRoot.get("color"));
                for (String color : params.get("color")) {
                    colorPredicate.value(color);
                }
            }

            CriteriaBuilder.In<String> modelPredicate = null;
            if (params.containsKey("model") && params.get("model") != null) {
                modelPredicate = cb.in(phoneRoot.get("model"));
                for (String model : params.get("model")) {
                    modelPredicate.value(model);
                }
            }

            CriteriaBuilder.In<String> osPredicate = null;
            if (params.containsKey("os") && params.get("os") != null) {
                osPredicate = cb.in(phoneRoot.get("os"));
                for (String os : params.get("os")) {
                    osPredicate.value(os);
                }
            }

            Predicate predicate = null;
            if (modelPredicate != null) {
                predicate = modelPredicate;
            }

            if (makerPredicate != null) {
                if (predicate != null) {
                    predicate = cb.and(predicate, makerPredicate);
                } else {
                    predicate = makerPredicate;
                }
            }

            if (colorPredicate != null) {
                if (predicate != null) {
                    predicate = cb.and(predicate, colorPredicate);
                } else {
                    predicate = colorPredicate;
                }
            }

            if (osPredicate != null) {
                if (predicate != null) {
                    predicate = cb.and(predicate, osPredicate);
                } else {
                    predicate = osPredicate;
                }
            }

            if (cmPredicate != null) {
                if (predicate != null) {
                    predicate = cb.and(predicate, cmPredicate);
                } else {
                    predicate = cmPredicate;
                }
            }

            if (predicate != null) {
                query.where(predicate);
                return session.createQuery(query).getResultList();
            } else {
                return session.createQuery(query).getResultList();
            }
        }
    }
}
