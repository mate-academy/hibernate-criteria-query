package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            throw new RuntimeException("Cant create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        List<Predicate> predicateList = new ArrayList<>();
        Set<String> keys = params.keySet();
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            for (String key:keys) {
                CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(key));
                for (int i = 0; i < params.get(key).length; i++) {
                    predicate.value(params.get(key)[i]);
                }
                predicateList.add(predicate);
            }
            query.where(cb.and(predicateList.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cant find by map" + params,e);
        }
    }
}
