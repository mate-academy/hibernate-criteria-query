package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
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
            throw new RuntimeException("Can't insert phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try(Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            if (params.containsKey("color")) {
                CriteriaBuilder.In<Object> colorsPredicate = cb.in(root.get("color"));
                for(String color: params.get("color")) {
                    colorsPredicate.value(color);
                }
                query.where(cb.and(colorsPredicate));
            }
            else if (params.containsKey("countryManufactured")) {
                CriteriaBuilder.In<Object> countryManufacturedPredicate =
                        cb.in(root.get("countryManufactured"));
                for (String manufacture: params.get("countryManufactured")){
                    countryManufacturedPredicate.value(manufacture);
                }
                query.where(cb.and(countryManufacturedPredicate));
            }
            else if (params.containsKey("os")) {
                CriteriaBuilder.In<Object> osPredicate = cb.in(root.get("os"));
                for (String os: params.get("os")) {
                    osPredicate.value(os);
                }
                query.where(cb.and(osPredicate));
            }
            else if (params.containsKey("model")) {
                CriteriaBuilder.In<Object> modelPredicate = cb.in(root.get("model"));
                for (String model: params.get("model")) {
                    modelPredicate.value(model);
                }
                query.where(cb.and(modelPredicate));
            }
            else if (params.containsKey("maker")) {
                CriteriaBuilder.In<Object> makerPredicate = cb.in(root.get("maker"));
                for (String maker: params.get("maker")) {
                    makerPredicate.value(maker);
                }
            }
            return session.createQuery(query).getResultList();
        }

    }
}
