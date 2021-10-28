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
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can`t create phone: " + phone, e);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate findAllPredicate = criteriaBuilder.and();
            for (Map.Entry<String, String[]> parameterEntry : params.entrySet()) {
                CriteriaBuilder.In<String> predicate = criteriaBuilder
                        .in(phoneRoot.get(parameterEntry.getKey()));
                for (String parameterValue : parameterEntry.getValue()) {
                    predicate.value(parameterValue);
                    findAllPredicate = criteriaBuilder.and(findAllPredicate, predicate);
                }
                query.where(findAllPredicate);
            }
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            String collect = params.entrySet().stream()
                    .map(p -> p.getKey() + " " + Arrays.toString(p.getValue()))
                    .collect(Collectors.joining("\n"));
            throw new RuntimeException("Can`t get phones with this parameters: " + collect, e);
        }
    }
}
