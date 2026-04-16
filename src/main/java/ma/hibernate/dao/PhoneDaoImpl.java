package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
            session.beginTransaction();
            session.save(phone);
            session.getTransaction().commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can not add phone to database", e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);

            List<Predicate> globalPredicates = new ArrayList<>();

            for (String paramName : params.keySet()) {
                String[] paramValues = params.get(paramName);

                List<Predicate> localPredicates = new ArrayList<>();

                for (String value : paramValues) {
                    localPredicates.add(cb.equal(root.get(paramName), value));
                }

                Predicate orPredicate = cb.or(localPredicates.toArray(new Predicate[0]));

                globalPredicates.add(orPredicate);
            }

            query.where(cb.and(globalPredicates.toArray(new Predicate[0])));

            return session.createQuery(query).getResultList();
        }
    }
}
