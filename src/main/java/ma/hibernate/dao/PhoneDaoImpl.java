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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can`t save phone " + phone + " to DB", e);
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
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            Predicate predicateX = null;
            int count = 0;
            for (Map.Entry<String, String[]> entry: params.entrySet()) {
                CriteriaBuilder.In<String> in = criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String val: entry.getValue()) {
                    in.value(val);
                }
                Predicate predicate = criteriaBuilder.and(in);
                predicateX = (count == 0
                        ? criteriaBuilder.and(predicate)
                        : criteriaBuilder.and(predicateX, predicate));
                ++count;
            }
            return session
                    .createQuery(predicateX != null
                            ? criteriaQuery.where(predicateX)
                            : criteriaQuery)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get list phones", e);
        }
    }
}
