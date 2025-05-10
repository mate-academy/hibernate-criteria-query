package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
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
            throw new RuntimeException("Can't create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);

            CriteriaBuilder.In<Object> spValuePredicate = null;
            Predicate resultPredicate = null;

            for (Map.Entry<String, String[]> param : params.entrySet()) {
                spValuePredicate = cb.in(phoneRoot.get(param.getKey()));
                Stream.of(param.getValue()).forEach(spValuePredicate::value);
                Predicate localPredicate = (resultPredicate != null)
                        ? resultPredicate : spValuePredicate;
                resultPredicate = cb.and(spValuePredicate, localPredicate);
            }
            CriteriaQuery<Phone> phoneCriteriaQuery = (resultPredicate == null)
                    ? criteriaQuery : criteriaQuery.where(resultPredicate);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        }
    }
}
