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
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> allPhoneQuery = cb.createQuery(Phone.class);
            Root<Phone> rootPhone = allPhoneQuery.from(Phone.class);
            Predicate allCondition = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> phonePredicate = cb.in(rootPhone.get(entry.getKey()));
                for (String paramValue : entry.getValue()) {
                    phonePredicate.value(paramValue);
                }
                allCondition = cb.and(allCondition, phonePredicate);
            }
            allPhoneQuery.where(allCondition);
            return session.createQuery(allPhoneQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phone", e);
        }
    }
}
