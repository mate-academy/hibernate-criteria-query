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
import org.hibernate.HibernateException;
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
            session = factory.openSession().getSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (HibernateException exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, exception);
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
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate inPredicate = cb.conjunction();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> inClause = cb.in(root.get(entry.getKey()));
                for (String option : entry.getValue()) {
                    inClause.value(option);
                }
                inPredicate = cb.and(inPredicate, inClause);
            }
            query.where(inPredicate);
            return session.createQuery(query).getResultList();
        } catch (HibernateException exception) {
            throw new RuntimeException("Couldn't find all phones with params: "
                    + params.entrySet().stream()
                    .map(v -> "(" + v.getKey() + Arrays.toString(v.getValue()) + ")")
                    .collect(Collectors.joining("; ")));
        }
    }
}
