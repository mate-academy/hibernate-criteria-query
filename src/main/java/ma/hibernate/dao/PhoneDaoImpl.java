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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone " + phone + " to DB" + e);
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phonesQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = phonesQuery.from(Phone.class);
            Predicate generalPredicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> phonePredicate = cb
                        .in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    phonePredicate.value(value);
                }
                generalPredicate = cb.and(generalPredicate, phonePredicate);
            }
            phonesQuery.where(generalPredicate);
            return session.createQuery(phonesQuery).getResultList();
        } catch (Exception e) {
            List<String> paramList = params.entrySet().stream()
                    .map(entry -> entry.getKey() + " :"
                            + Arrays.toString(entry.getValue()))
                    .collect(Collectors.toList());
            throw new RuntimeException("Can't get all phones from DB " + paramList, e);
        }
    }
}
