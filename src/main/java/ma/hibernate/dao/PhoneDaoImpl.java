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
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save phone in DB", e);
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            CriteriaBuilder.In<String> countryManufacturedPredicate = null;
            List<Predicate> predicateList = new ArrayList<>();
            if (params.containsKey("countryManufactured")) {
                countryManufacturedPredicate =
                        cb.in(phoneRoot.get("countryManufactured"));
                for (String countryManufactured : params.get("countryManufactured")) {
                    countryManufacturedPredicate.value(countryManufactured);
                }
                predicateList.add(countryManufacturedPredicate);
            }
            CriteriaBuilder.In<String> makerPredicate = null;
            if (params.containsKey("maker")) {
                makerPredicate = cb.in(phoneRoot.get("maker"));
                for (String maker : params.get("maker")) {
                    makerPredicate.value(maker);
                }
                predicateList.add(makerPredicate);
            }
            CriteriaBuilder.In<String> colorPredicate = null;
            if (params.containsKey("color")) {
                colorPredicate = cb.in(phoneRoot.get("color"));
                for (String color : params.get("color")) {
                    colorPredicate.value(color);
                }
                predicateList.add(colorPredicate);
            }
            CriteriaBuilder.In<String> modelPredicate = null;
            if (params.containsKey("model")) {
                modelPredicate = cb.in(phoneRoot.get("model"));
                for (String model : params.get("model")) {
                    modelPredicate.value(model);
                }
                predicateList.add(modelPredicate);
            }
            if (predicateList.isEmpty()) {
                return session.createQuery(query).getResultList();
            }
            Predicate predicate;
            switch (predicateList.size()) {
                case 1:
                    predicate = predicateList.get(0);
                    break;
                case 2:
                    predicate = cb.and(predicateList.get(0), predicateList.get(1));
                    break;
                case 3:
                    predicate = cb.and(predicateList.get(0), predicateList.get(1),
                            predicateList.get(2));
                    break;
                default:
                    predicate = cb.and(predicateList.get(0), predicateList.get(1),
                            predicateList.get(2), predicateList.get(3));
                    break;
            }
            query.where(predicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get phones from DB", e);
        }
    }
}
