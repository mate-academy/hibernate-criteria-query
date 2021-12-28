package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
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
            Predicate conjunctionOfClauses = cb.conjunction();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> inClause = cb.in(root.get(entry.getKey()));
                for (String option : entry.getValue()) {
                    inClause.value(option);
                }
                conjunctionOfClauses = cb.and(conjunctionOfClauses, inClause);
            }
            query.where(conjunctionOfClauses);
            return session.createQuery(query).getResultList();
        } catch (HibernateException exception) {
            throw new RuntimeException("Couldn't find all phones with params: "
                    + convertToString(params));
        }
    }

    private String convertToString(Map<String, String[]> params) {
        StringBuilder paramsInString = new StringBuilder();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            paramsInString.append("( ").append(entry.getKey())
                    .append(" ");
            for (String value : entry.getValue()) {
                paramsInString.append(value).append(", ");
            }
            paramsInString.append(")");
        }
        return paramsInString.toString();
    }
}
