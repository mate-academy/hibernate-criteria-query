package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import ma.hibernate.exeption.DataProcessingException;
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
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't save phone to DB",e);
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
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = params.entrySet().stream()
                    .map(entry -> buildPredicate(cb, root, entry))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

            return session.createQuery(query).getResultList();
        } catch (HibernateException e) {
            throw new DataProcessingException("Can't find phone",e);
        }
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Root<Phone> root,
                                     Map.Entry<String, String[]> entry) {
        return Optional.ofNullable(entry.getValue())
                .filter(values -> values.length > 0)
                .map(values -> values.length == 1
                        ? cb.equal(root.get(entry.getKey()), values[0])
                        : root.get(entry.getKey()).in((Object[]) values))
                .orElse(null);
    }
}
