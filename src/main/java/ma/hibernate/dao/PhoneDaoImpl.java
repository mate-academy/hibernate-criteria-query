package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
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
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            var phoneRoot = criteriaQuery.from(Phone.class);

            JpaPredicate[] predicates = params.entrySet().stream()
                    .map(entry -> phoneRoot.get(entry.getKey()).in((Object[]) entry.getValue()))
                    .toArray(JpaPredicate[]::new);

            criteriaQuery.where(predicates);

            Query<Phone> query = session.createQuery(criteriaQuery);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones with params " + params, e);
        }
    }
}
