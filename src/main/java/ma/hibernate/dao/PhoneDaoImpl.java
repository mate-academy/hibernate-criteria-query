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
            throw new RuntimeException("Can`t create phone to DB " + phone, e);
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
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            Predicate predicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> paramPredicate = cb.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    paramPredicate.value(value);
                }
                predicate = cb.and(predicate, paramPredicate);
            }
            criteriaQuery.where(predicate);

//
//            CriteriaBuilder.In<String> countryManufacturedPredicate
//                    = cb.in(phoneRoot.get("countryManufactured"));
//            CriteriaBuilder.In<String> modelPredicate = cb.in(phoneRoot.get("model"));
//            CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
//            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
//            CriteriaBuilder.In<String> osPredicate = cb.in(phoneRoot.get("os"));
//            String[] models = null;
//            String[] countryManufactureds = null;
//            String[] makers = null;
//            String[] colors = null;
//            String[] oss = null;
//            if (params.containsKey("countryManufactured")) {
//                countryManufactureds = params.get("countryManufactured");
//                for (String manufactured : countryManufactureds) {
//                    countryManufacturedPredicate.value(manufactured);
//                }
//            }
//            if (params.containsKey("model")) {
//                models = params.get("model");
//                for (String model : models) {
//                    modelPredicate.value(model);
//                }
//            }
//            if (params.containsKey("maker")) {
//                makers = params.get("maker");
//                for (String maker : makers) {
//                    makerPredicate.value(maker);
//                }
//            }
//            if (params.containsKey("color")) {
//                colors = params.get("color");
//                for (String color : colors) {
//                    colorPredicate.value(color);
//                }
//            }
//            if (params.containsKey("os")) {
//                oss = params.get("os");
//                for (String os : oss) {
//                    colorPredicate.value(os);
//                }
//            }
//            criteriaQuery.where(cb.or(
////                    countryManufacturedPredicate,
//                            modelPredicate,
//                            makerPredicate,
//
////                    osPredicate,
////                    colorPredicate,
//                            cb.and(makerPredicate, colorPredicate),
//                            cb.and(makerPredicate, colorPredicate, countryManufacturedPredicate)
//                //select, * from phones where
//                    )
//            );
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can`t find all with parameters" + params, e);
        }
    }
}
