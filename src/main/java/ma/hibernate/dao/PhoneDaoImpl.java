package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.Arrays;
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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
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
            if (params.isEmpty()) {
                query.select(phoneRoot);
                return session.createQuery(query).getResultList();
            }
            CriteriaBuilder.In<String> modelPredicate = cb.in(phoneRoot.get("model"));
            CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
            CriteriaBuilder.In<String> osPredicate = cb.in(phoneRoot.get("os"));
            CriteriaBuilder.In<String> countryPredicate =
                    cb.in(phoneRoot.get("countryManufactured"));
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> mapEntry : params.entrySet()) {
                String parameter = mapEntry.getKey();
                switch (parameter) {
                    case "model" : {
                        Arrays.stream(mapEntry.getValue()).forEach(modelPredicate::value);
                        predicates.add(modelPredicate);
                        break;
                    }
                    case "maker" : {
                        Arrays.stream(mapEntry.getValue()).forEach(makerPredicate::value);
                        predicates.add(makerPredicate);
                        break;
                    }
                    case "color": {
                        Arrays.stream(mapEntry.getValue()).forEach(colorPredicate::value);
                        predicates.add(colorPredicate);
                        break;
                    }
                    case "os": {
                        Arrays.stream(mapEntry.getValue()).forEach(osPredicate::value);
                        predicates.add(osPredicate);
                        break;
                    }
                    case "countryManufactured" :
                    default: {
                        Arrays.stream(mapEntry.getValue()).forEach(countryPredicate::value);
                        predicates.add(countryPredicate);
                        break;
                    }
                }
            }
            query.where(cb.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get a phone by this query", e);
        }
    }
}
