package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can't create phone for: " + phone, e);
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
            Root<Phone> phoneRoot = query.from(Phone.class);
            query.where(params.entrySet().stream()
                    .map(entry -> {
                        String key = entry.getKey();
                        String[] values = entry.getValue();
                        Expression<String> expression = phoneRoot.get(key);
                        return expression.in((Object[]) values);
                    }).toArray(Predicate[]::new));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            String paramsString = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Can't find phones for params: " + paramsString, e);
        }
    }
}
