package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.protobuf.MapEntry;
import ma.hibernate.exception.DataProcessingException;
import ma.hibernate.model.Phone;
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
            throw new DataProcessingException("Can't insert phone " + phone, e);
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

            CriteriaBuilder.In<String> producerIdPredicate = cb.in(phoneRoot.get("maker"));
            params.entrySet().stream().filter(p -> p.getKey().equals("maker"))
                    .map(Map.Entry::getValue)
                    .flatMap(array -> Arrays.stream(array))
                    .forEach(producerIdPredicate::value);

            CriteriaBuilder.In<String> modelPredicate = cb.in(phoneRoot.get("model"));
            params.entrySet().stream().filter(p -> p.getKey().equals("model"))
                    .map(Map.Entry::getValue)
                    .flatMap(Arrays::stream)
                    .forEach(modelPredicate::value);

            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
            params.entrySet().stream().filter(p -> p.getKey().equals("color"))
                    .map(Map.Entry::getValue)
                    .flatMap(Arrays::stream)
                    .forEach(colorPredicate::value);

            query.where(cb.and(producerIdPredicate, modelPredicate, colorPredicate));
            return session.createQuery(query).getResultList();
        }
    }
}
