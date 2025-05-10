package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
            throw new RuntimeException("Can't add phone " + phone + " to the DB. ", e);
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
            CriteriaQuery<Phone> findAllPhonesQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllPhonesQuery.from(Phone.class);
            Predicate allCriteriaPredicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> modelPredicate = cb.in(phoneRoot.get(entry.getKey()));
                for (String model : entry.getValue()) {
                    modelPredicate.value(model);
                }
                allCriteriaPredicate = cb.and(allCriteriaPredicate, modelPredicate);
            }
            findAllPhonesQuery.where(allCriteriaPredicate);
            return session.createQuery(findAllPhonesQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get phones by the criteria: "
                    + requiredCriteria(params), e);
        }
    }

    private String requiredCriteria(Map<String, String[]> params) {
        return params.entrySet().stream()
                .flatMap(p -> Arrays.stream(p.getValue()))
                .collect(Collectors.joining(", "));
    }
}
