package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ma.hibernate.model.Phone;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;
        try (var session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(String.format(
                    "Can`t add a phone %s to the DB", phone
            ), ex);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (var session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Set<Predicate> predicates = new HashSet<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String param = entry.getKey();
                Path<String> paramPath = phoneRoot.get(param);
                CriteriaBuilder.In<String> inPredicate = cb.in(paramPath);
                for (String value : entry.getValue()) {
                    inPredicate.value(value);
                }
                predicates.add(inPredicate);
            }

            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Can`t get phones from DB with provided parameters");
        }
    }
}
