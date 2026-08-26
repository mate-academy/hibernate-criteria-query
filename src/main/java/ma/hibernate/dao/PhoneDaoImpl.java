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
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone " + phone + " to DB", e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate[] arrayPredicate = new Predicate[params.size()];
            List<String> keys = new ArrayList<>(params.keySet());
            for (int i = 0; i < params.size(); i++) {
                CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(keys.get(i)));
                arrayPredicate[i] = predicate;
                for (String str : params.get(keys.get(i))) {
                    predicate.value(str);
                }
            }
            query.where(cb.and(arrayPredicate));
            return session.createQuery(query).getResultList();
        }
    }
}
