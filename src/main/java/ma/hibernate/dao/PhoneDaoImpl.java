package ma.hibernate.dao;

import java.util.ArrayList;
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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
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
            List<Predicate> predicates = new ArrayList<>(params.size());

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> in = cb.in(root.get(entry.getKey()));
                Arrays.stream(entry.getValue()).forEach(in::value);
                predicates.add(in);
            }
            query.where(cb.and(predicates.toArray(Predicate[]::new)));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones with parameters: "
            + params.entrySet().stream()
                    .map(entry -> entry.getKey() + " : "
                            + Arrays.toString(entry.getValue()))
                    .collect(Collectors.joining(System.lineSeparator())), e);
        }

    }
}
