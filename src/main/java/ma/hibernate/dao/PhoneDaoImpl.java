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
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error insert data into phones " + phone, e);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>(params.size());
            for (Map.Entry<String, String[]> element : params.entrySet()) {
                CriteriaBuilder.In<String> predicate =
                        criteriaBuilder.in(root.get(element.getKey()));
                for (String value : element.getValue()) {
                    predicate.value(value);
                }
                predicates.add(predicate);
            }
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error get data from table phone for parameters "
                    + convertMapToString(params), e);
        }
    }

    private String convertMapToString(Map<String, String[]> map) {
        String result = map.keySet().stream()
                .map(key -> key + "=" + Arrays.toString(map.get(key)))
                .collect(Collectors.joining(", ", "{", "}"));
        return result;
    }
}
