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
            throw new RuntimeException("Can't save current phone into DB: " + phone, e);
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
            CriteriaQuery<Phone> findPhonesListByParamsQuery = criteriaBuilder
                    .createQuery(Phone.class);
            Root<Phone> phoneRoot = findPhonesListByParamsQuery.from(Phone.class);
            Predicate predicate = criteriaBuilder.and();

            for (String key : params.keySet()) {
                CriteriaBuilder.In<String> currentKeyPredicate = criteriaBuilder
                        .in(phoneRoot.get(key));
                for (String value : params.get(key)) {
                    currentKeyPredicate.value(value);
                }
                predicate = criteriaBuilder.and(predicate, currentKeyPredicate);
            }

            findPhonesListByParamsQuery.where(predicate);
            return session.createQuery(findPhonesListByParamsQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't take a list of phones by input params: "
                    + params, e);
        }
    }
}
