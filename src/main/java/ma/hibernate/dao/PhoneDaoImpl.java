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
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save phone to DB" + phone, e);
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
            Root<Phone> phoneRoot = query.from(Phone.class);

            Predicate phonePredicate = criteriaBuilder.and();
            for (Map.Entry pair : params.entrySet()) {
                CriteriaBuilder.In<String> in =
                        criteriaBuilder.in(phoneRoot.get((String) pair.getKey()));
                for (String predicateType : (String[]) pair.getValue()) {
                    in.value(predicateType);
                }
                phonePredicate = criteriaBuilder.and(phonePredicate, in);
            }
            query.where(phonePredicate);
            return session.createQuery(query).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Can't get phones by this criteria " + params, e);
        }
    }
}
