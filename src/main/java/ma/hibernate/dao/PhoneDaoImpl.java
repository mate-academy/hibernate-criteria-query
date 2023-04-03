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
            throw new RuntimeException("Can't insert phone " + phone, e);
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
            CriteriaQuery<Phone> phoneCriteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneCriteriaQuery.from(Phone.class);
            List<Predicate> predicateList = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> phoneParams =
                        criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String phoneValue : entry.getValue()) {
                    phoneParams.value(phoneValue);
                }
                predicateList.add(phoneParams);
            }
            phoneCriteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[0])));
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't find all phones", e);
        }
    }
}
