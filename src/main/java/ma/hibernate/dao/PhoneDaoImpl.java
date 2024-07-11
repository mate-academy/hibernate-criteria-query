package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);

            Predicate[] predicates = params.entrySet().stream()
                .flatMap(entry -> {
                    String key = entry.getKey();
                    String[] values = entry.getValue();
                    return Arrays.stream(values).map(value -> cb.equal(root.get(key), value));
                })
                .toArray(Predicate[]::new);

            cq.where(cb.or(predicates));
            Query<Phone> query = session.createQuery(cq);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones with params " + params, e);
        }
    }
}
