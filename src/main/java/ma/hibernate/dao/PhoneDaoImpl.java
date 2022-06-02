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
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            List<CriteriaBuilder.In<String>> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> on = cb.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    on.value(value);
                }
                predicates.add(on);
            }
            Predicate predicate = cb.and(predicates.toArray(Predicate[]::new));
            criteriaQuery.where(predicate);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phone " + params, e);
        }
    }
}
