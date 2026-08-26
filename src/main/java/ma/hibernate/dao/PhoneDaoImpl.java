package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    private static final Map<String, Function<Root<Phone>,
            Object>> FIELD_MAPPINGS = new HashMap<>();

    static {
        FIELD_MAPPINGS.put("model", root -> root.get("model"));
        FIELD_MAPPINGS.put("maker", root -> root.get("maker"));
        FIELD_MAPPINGS.put("color", root -> root.get("color"));
        FIELD_MAPPINGS.put("countryManufactured", root -> root.get("countryManufactured"));
    }

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.save(phone);
            session.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Can't create phone " + phone, e);
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                Function<Root<Phone>, Object> fieldMapping = FIELD_MAPPINGS.get(key);
                Path<Object> path = (Path<Object>) fieldMapping.apply(root);
                Predicate predicate = path.in((Object[]) values);
                predicates.add(predicate);
            }
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching phones with parameters: " + params, e);
        }
    }
}
