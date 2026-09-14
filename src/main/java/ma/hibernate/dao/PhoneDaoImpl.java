package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(phone);
                transaction.commit();
                return phone;
            } catch (RuntimeException e) {
                try {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                } catch (RuntimeException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
                throw e;
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't create phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicates.add(root.get(entry.getKey()).in(Arrays.asList(entry.getValue())));
            }
            query.select(root).where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException("Can't find phones by parameters "
                    + params.entrySet().stream()
                            .map(entry -> entry.getKey() + "=" + Arrays.toString(entry.getValue()))
                            .collect(Collectors.joining(", ", "{", "}")), e);
        }
    }
}
