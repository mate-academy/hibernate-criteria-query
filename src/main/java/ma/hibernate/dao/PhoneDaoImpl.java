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
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save phone " + phone, e);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> findAllPhonesQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllPhonesQuery.from(Phone.class);
            Predicate predicate = criteriaBuilder.conjunction();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicate = criteriaBuilder.and(predicate, phoneRoot.get(entry.getKey())
                        .in(entry.getValue()));
            }
            findAllPhonesQuery.where(predicate);
            return session.createQuery(findAllPhonesQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can`t find all phones by parameters: "
                                    + params.keySet(), e);
        }
    }
}
