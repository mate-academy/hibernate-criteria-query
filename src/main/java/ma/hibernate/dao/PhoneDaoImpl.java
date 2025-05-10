package ma.hibernate.dao;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        SessionFactory sessionFactory = factory;
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate criteria = cb.conjunction();
            Iterator<Map.Entry<String, String[]>> itr = params.entrySet().iterator();
            while (itr.hasNext()) {
                String item = itr.next().getKey();
                CriteriaBuilder.In<String> finalPredicate = cb.in(phoneRoot.get(item));
                Arrays.stream(params.get(item)).forEach(p -> finalPredicate.value(p));
                criteria = cb.and(criteria, finalPredicate);
            }
            query.where(criteria);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all by "
                    + params.entrySet()
                    .stream()
                    .map(r -> r.getKey() + " = "
                            + Arrays.stream(r.getValue()).collect(Collectors.joining(", ")))
                    .collect(Collectors.joining(" / ")), e);
        }
    }
}
