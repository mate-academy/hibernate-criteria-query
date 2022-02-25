package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save phone: " + phone, e);
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
            CriteriaQuery<Phone> phoneCriteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneCriteriaQuery.from(Phone.class);
            Predicate phonePredicate = criteriaBuilder.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> in = criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String paramValue : entry.getValue()) {
                    in.value(paramValue);
                }
                phonePredicate = criteriaBuilder.and(phonePredicate, in);
            }
            phoneCriteriaQuery.where(phonePredicate);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception e) {
            String stringParams = params.entrySet().stream()
                    .map(set -> "{" + set.getKey() + " : " + Arrays.toString(set.getValue()) + "}")
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Can't get phones by params: " + stringParams);
        }
    }
}
