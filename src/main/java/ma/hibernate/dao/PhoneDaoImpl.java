package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        CriteriaBuilder criteriaBuilder;
        try (Session session = factory.openSession()) {
            criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();
            for (var entry : params.entrySet()) {
                String paramName = entry.getKey();
                String[] paramValues = entry.getValue();
                CriteriaBuilder.In<String> in = criteriaBuilder.in(phoneRoot.get(paramName));
                Arrays.stream(paramValues).forEach(in::value);
                predicates.add(in);
            }

            Predicate[] array = predicates.toArray(predicates.toArray(new Predicate[0]));
            criteriaBuilder.and(array);
            query.where(array);
            return session.createQuery(query).getResultList();

        } catch (Exception ex) {
            throw new RuntimeException("Can not get phones", ex);
        }
    }
}
