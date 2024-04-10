package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try (Session session = factory.openSession()) {
            session.save(phone);
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can't create phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> findAllQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> rootPhone = findAllQuery.from(Phone.class);
            List<Predicate> whereConditions = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> inCondition
                        = criteriaBuilder.in(rootPhone.get(entry.getKey()));
                for (String option : entry.getValue()) {
                    inCondition.value(option);
                }
                whereConditions.add(inCondition);
            }
            findAllQuery.where(criteriaBuilder.and(whereConditions.toArray(Predicate[]::new)));
            return session.createQuery(findAllQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get phones by params: " + params.keySet().stream()
                    .map(key -> key + "=" + Arrays.toString(params.get(key)))
                    .collect(Collectors.joining(", ", "{", "}")), e);
        }
    }
}
