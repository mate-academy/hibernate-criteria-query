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
            session.persist(phone);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> andPredicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String[] values = entry.getValue();
                List<Predicate> orPredicates = new ArrayList<>();
                for (String value : values) {
                    Predicate orPredicate = criteriaBuilder
                            .equal(root.get(entry.getKey()), value);
                    orPredicates.add(orPredicate);
                }
                Predicate andPredicate = criteriaBuilder.or(orPredicates
                        .toArray(new Predicate[]{}));
                andPredicates.add(andPredicate);
            }
            Predicate finalPredicate = criteriaBuilder.and(andPredicates
                    .toArray(new Predicate[]{}));
            query.select(root).where(finalPredicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find  all phone for this params: " + params, e);
        }
    }
}
