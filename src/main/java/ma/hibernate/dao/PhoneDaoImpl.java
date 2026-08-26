package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.HibernateException;
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
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.commit();
            }
            throw new RuntimeException("Failed to add phone to db" + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);
            List<Predicate> predicates = buildPredicates(params, cb, root);
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(criteriaQuery).getResultList();
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to retrieve phones from db", e);
        }
    }

    private List<Predicate> buildPredicates(Map<String, String[]> params,
                                            CriteriaBuilder cb, Root<Phone> root) {
        List<Predicate> predicateList = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String parameter = entry.getKey();
            Path<String> objectPath = root.get(parameter);
            CriteriaBuilder.In<String> inPredicate = cb.in(objectPath);
            for (String value : entry.getValue()) {
                inPredicate.value(value);
            }
            predicateList.add(inPredicate);
        }
        return predicateList;
    }
}
