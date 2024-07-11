package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPredicate;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.save(phone);
            session.getTransaction().commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can't create phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            var root = cq.from(Phone.class);

            List<JpaPredicate> predicates = params.entrySet().stream()
                    .flatMap(entry -> {
                        String key = entry.getKey();
                        String[] values = entry.getValue();
                        return Arrays.stream(values)
                                .map(value -> cb.equal(root.get(key), value));
                    })
                    .collect(Collectors.toList());

            cq.where(predicates.isEmpty() ? cb.conjunction() : 
                     cb.or(predicates.toArray(new JpaPredicate[0])));

            Query<Phone> query = session.createQuery(cq);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones with params " + params, e);
        }
    }
}
