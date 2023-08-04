package ma.hibernate.dao;

import java.util.HashMap;
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
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Could not create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {

        Session session = factory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
        Root<Phone> phoneRoot = query.from(Phone.class);
        CriteriaBuilder.In<String> countryManufacturedPredicate =
                criteriaBuilder.in(phoneRoot.get("countryManufactured"));
        CriteriaBuilder.In<String> makerPredicate = criteriaBuilder.in(phoneRoot.get("maker"));
        CriteriaBuilder.In<String> colorPredicate = criteriaBuilder.in(phoneRoot.get("color"));
        CriteriaBuilder.In<String> modelPredicate = criteriaBuilder.in(phoneRoot.get("model"));
        Map<String, CriteriaBuilder.In<String>> map = new HashMap<>();
        map.put("countryManufactured", countryManufacturedPredicate);
        map.put("maker", makerPredicate);
        map.put("color", colorPredicate);
        map.put("model", modelPredicate);

        Predicate[] array = params.entrySet().stream()
                                    .filter((stringEntry -> map.containsKey(stringEntry.getKey())))
                                    .map(stringEntry -> map.compute(stringEntry.getKey(),
                                            ((s, stringIn) -> {
                                                for (String val : stringEntry.getValue()) {
                                                    stringIn.value(val);
                                                }
                                                return stringIn;
                                            }))
                                    )
                                    .toArray(Predicate[]::new);
        query.select(phoneRoot)
                .where(criteriaBuilder.and(array));
        return session.createQuery(query).getResultList();
    }
}
