package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
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
            session = super.factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save phone in DB");
        } finally {
            session.close();
        }

        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {

        try (Session session = super.factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            Predicate finalPredicate = criteriaBuilder.conjunction();
            for (Map.Entry<String, String[]> entry: params.entrySet()) {
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String param: entry.getValue()) {
                    in.value(param);
                }
                finalPredicate = criteriaBuilder.and(finalPredicate, in);
            }
            criteriaQuery.where(finalPredicate);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones with criteria in DB");
        }
    }
}
