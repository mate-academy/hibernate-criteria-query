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
        Session session = null;
        Transaction transaction = null;
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
            throw new RuntimeException("Can't insert phone to DB " + phone);
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
            CriteriaQuery<Phone> findAllQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllQuery.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> inPredicate = cb.in(phoneRoot.get(entry.getKey()));
                for (String paramValue : entry.getValue()) {
                    inPredicate.value(paramValue);
                }
                predicates.add(inPredicate);
            }
            findAllQuery.where(predicates.toArray(new Predicate[]{}));
            return session.createQuery(findAllQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones by specified parameters");
        }
    }
}
