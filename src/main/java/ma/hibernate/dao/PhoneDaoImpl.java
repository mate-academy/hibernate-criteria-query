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
            CriteriaBuilder.In<String> countryManufacturedPredicate
                    = cb.in(phoneRoot.get("countryManufactured"));
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                switch (entry.getKey()) {
                    case "model" : {
                        for (String value : entry.getValue()) {
                            modelPredicate.value(value);
                        }
                        predicates.add(modelPredicate);
                        break;
                    }
                    case "maker" : {
                        for (String value : entry.getValue()) {
                            makerPredicate.value(value);
                        }
                        predicates.add(makerPredicate);
                        break;
                    }
                    case "color" : {
                        for (String value : entry.getValue()) {
                            colorPredicate.value(value);
                        }
                        predicates.add(colorPredicate);
                        break;
                    }
                    case "os" : {
                        for (String value : entry.getValue()) {
                            osPredicate.value(value);
                        }
                        predicates.add(osPredicate);
                        break;
                    }
                    case "countryManufactured" :
                    default: {
                        for (String value : entry.getValue()) {
                            countryManufacturedPredicate.value(value);
                        }
                        predicates.add(countryManufacturedPredicate);
                        break;
                    }
                }
            }
            query.where(cb.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all phones for this query");
        }
    }
}
