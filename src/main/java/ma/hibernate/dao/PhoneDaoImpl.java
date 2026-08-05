package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone to DB. Phone: " + phone, e);
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
            CriteriaBuilder.In<String>[] predicates = new CriteriaBuilder.In[params.size()];
            int index = 0;
            for (Map.Entry<String, String[]> pair : params.entrySet()) {
                CriteriaBuilder.In<String> paramsPredicate = cb.in(phoneRoot.get(pair.getKey()));
                for (String value : pair.getValue()) {
                    paramsPredicate.value(value);
                }
                predicates[index] = paramsPredicate;
                index++;
            }
            query.where(cb.and(predicates));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get phones by params: "
                    + params.entrySet().stream()
                        .map(pair -> pair.getKey() + ":" + Arrays.toString(pair.getValue()))
                        .collect(Collectors.joining(", ")), e);
        }
    }
}
