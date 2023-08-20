package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create the phone in the DB: " + phone);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            /*
            SELECT * FROM PHONE
            WHERE FIELD_VALUE IN VALUES
             */
            int numberOfFieldsWithParams = params.size();
            List<Predicate> predicates = new ArrayList<>(numberOfFieldsWithParams);

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<Object> predicate = criteriaBuilder.in(phoneRoot
                                                                            .get(entry.getKey()));
                Stream.of(entry.getValue()).forEach(predicate::value);
                predicates.add(predicate);
            }

            query.where(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones with params: "
                    + params.entrySet()
                    .stream()
                    .map(entrySet -> entrySet.getKey() + ": "
                            + Arrays.toString(entrySet.getValue()))
                    .collect(Collectors.joining("\n"))
            );
        }
    }
}
