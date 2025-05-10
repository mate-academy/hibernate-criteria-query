package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ma.hibernate.exception.DataProcessingException;
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
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert in DB phone " + phone, e);
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

            // initialize commonPredicate always true
            Predicate commonPredicate = cb.conjunction();

            for (Map.Entry<String, String[]> paramEntry : params.entrySet()) {
                String paramName = paramEntry.getKey();
                String[] paramValues = paramEntry.getValue();

                CriteriaBuilder.In<String> paramEntryInValuePredicate =
                        cb.in(phoneRoot.get(paramName));
                for (String paramValue: paramValues) {
                    paramEntryInValuePredicate.value(paramValue);
                }
                commonPredicate = cb.and(commonPredicate, paramEntryInValuePredicate);
            }

            criteriaQuery.where(commonPredicate);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (RuntimeException e) {
            throw new DataProcessingException(
                    "Can't find in DB all phones with parameters names: " + params.keySet()
                            + " and values: " + params.values().stream()
                                    .flatMap(Arrays::stream)
                                    .collect(Collectors.joining(", ","[","]")),
                    e);
        }
    }
}
