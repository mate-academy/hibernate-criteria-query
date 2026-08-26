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
        Session session = null;
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
            throw new RuntimeException("Can't add phone to DB: " + phone, e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneCriteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneCriteriaQuery.from(Phone.class);
            List<Predicate> phonePredicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> predicate =
                        criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String predicateValue : entry.getValue()) {
                    predicate.value(predicateValue);
                }
                phonePredicates.add(predicate);
            }
            phoneCriteriaQuery
                    .where(criteriaBuilder.and(phonePredicates.toArray(new Predicate[0])));
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all Phones!", e);
        }
    }
}
