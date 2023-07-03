package ma.hibernate.dao;

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

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try (Session session = factory.openSession()) {
            session.save(phone);
        } catch (Exception e) {
            throw new RuntimeException("Can't create phone " + phone, e);
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate resultPredicate = criteriaBuilder.and(params.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .map(entry -> {
                        String key = entry.getKey();
                        String[] values = entry.getValue();
                        CriteriaBuilder.In<String> predicate =
                                criteriaBuilder.in(phoneRoot.get(key));
                        Arrays.stream(values).forEach(predicate::value);
                        return predicate;
                    }).toArray(Predicate[]::new));
            query.where(resultPredicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find All Phones with parameters: " + params, e);
        }
    }
}
