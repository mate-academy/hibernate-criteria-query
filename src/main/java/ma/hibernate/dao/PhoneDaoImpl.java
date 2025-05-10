package ma.hibernate.dao;

import java.util.ArrayList;
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
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Couldn't add phone " + phone + " to db", e);
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
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate mainPredicate = cb.and();
            for (Map.Entry<String, String[]> phoneEntry : params.entrySet()) {
                CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(phoneEntry.getKey()));
                for (String parameter : phoneEntry.getValue()) {
                    predicate.value(parameter);
                }
                mainPredicate = cb.and(mainPredicate, predicate);
            }
            query.where(mainPredicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            List<String> stringParams = new ArrayList<>();
            params.forEach((c, v) -> stringParams.add(c + ":" + Arrays.toString(v)));
            throw new RuntimeException("Can't find all phones by this parameter: "
                    + stringParams, e);
        }
    }
}
