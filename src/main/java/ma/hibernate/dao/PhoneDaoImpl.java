package ma.hibernate.dao;

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
            if (session != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create new phone in DB " + phone, e);
        } finally {
            if (transaction != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneCriteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneCriteriaQuery.from(Phone.class);
            Predicate[] cbList = new Predicate[params.size()];
            int i = 0;
            for (String mapKey : params.keySet()) {
                CriteriaBuilder.In<String> keyPredicate = cb.in(phoneRoot.get(mapKey));
                for (String param : params.get(mapKey)) {
                    keyPredicate.value(param);
                }
                cbList[i] = keyPredicate;
                i++;
            }
            Predicate phonePredicate = cb.and(cbList);
            phoneCriteriaQuery.where(phonePredicate);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all phones from DB", e);
        }
    }
}
