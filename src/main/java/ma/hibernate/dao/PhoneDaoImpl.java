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
            throw new RuntimeException("Can't create phone "
                    + phone + ". ", e);
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
            CriteriaQuery<Phone> findPhonesQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = findPhonesQuery.from(Phone.class);
            Predicate allParamPredicate = cb.and();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                CriteriaBuilder.In<Object> paramPredicate = cb.in(phoneRoot.get(param.getKey()));
                for (String value : param.getValue()) {
                    paramPredicate.value(value);
                }
                allParamPredicate = cb.and(allParamPredicate, paramPredicate);
            }
            findPhonesQuery.where(allParamPredicate);
            return session.createQuery(findPhonesQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones with parameters "
                    + convertParams(params) + ". ", e);
        }
    }

    private String convertParams(Map<String, String[]> params) {
        return params.entrySet()
                .stream()
                .map(m -> m.getKey() + "=" + Arrays.toString(m.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
