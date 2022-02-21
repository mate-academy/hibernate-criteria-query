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
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone to DB." + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneQuery.from(Phone.class);

            CriteriaBuilder.In<String> modelPredicate = cb.in(phoneRoot.get("model"));
            CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
            CriteriaBuilder.In<String> osPredicate = cb.in(phoneRoot.get("os"));
            CriteriaBuilder.In<String> countryPredicate
                    = cb.in(phoneRoot.get("countryManufactured"));

            List<Predicate> conditions = new ArrayList<>();

            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String[] paramsArray = param.getValue();
                for (String paramCurrentValue : paramsArray) {
                    switch (param.getKey()) {
                        case "model":
                            modelPredicate.value(paramCurrentValue);
                            conditions.add(modelPredicate);
                            break;
                        case "maker":
                            makerPredicate.value(paramCurrentValue);
                            conditions.add(makerPredicate);
                            break;
                        case "color":
                            colorPredicate.value(paramCurrentValue);
                            conditions.add(colorPredicate);
                            break;
                        case "os":
                            osPredicate.value(paramCurrentValue);
                            conditions.add(osPredicate);
                            break;
                        case "countryManufactured":
                            countryPredicate.value(paramCurrentValue);
                            conditions.add(countryPredicate);
                            break;
                        default:
                            break;
                    }
                }
            }
            return session.createQuery(phoneQuery.select(phoneRoot)
                 .where(conditions.toArray(new Predicate[] {})))
                 .getResultList();
        }
    }
}
