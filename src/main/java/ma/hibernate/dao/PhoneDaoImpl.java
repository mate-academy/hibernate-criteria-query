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
            throw new RuntimeException("Can't insert phone to DB " + phone, e);
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
            CriteriaQuery<Phone> findAllPhonesQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllPhonesQuery.from(Phone.class);
            List<Predicate> criterias = new ArrayList<>();
            for (Map.Entry<String, String[]> currentPair : params.entrySet()) {
                CriteriaBuilder.In<String> currentCriteria =
                        criteriaBuilder.in(phoneRoot.get(currentPair.getKey()));
                for (String currentValue : currentPair.getValue()) {
                    currentCriteria.value(currentValue);
                }
                criterias.add(currentCriteria);
            }
            findAllPhonesQuery.where(criteriaBuilder.and(criterias.toArray(new Predicate[0])));
            return session.createQuery(findAllPhonesQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones by given criteria.", e);
        }
    }
}
