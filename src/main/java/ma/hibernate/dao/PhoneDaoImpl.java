package ma.hibernate.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = factory.openSession();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(phone);
            entityTransaction.commit();
            return phone;
        } catch (Exception e) {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw new RuntimeException("Exception while creating phone " + phone, e);
        } finally {
            if (entityTransaction != null) {
                entityManager.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (EntityManager entityManager = factory.openSession()) {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> resultPredicate = new ArrayList<>();

            params.forEach((key, value) ->
                    resultPredicate.add(root.get(key).in(Arrays.asList(value))));
            query.select(root).where(resultPredicate.toArray(new Predicate[0]));
            return entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            String stringParams = params.entrySet()
                    .stream()
                    .map(elem -> elem.getKey() + ": " + Arrays.toString(elem.getValue()))
                    .collect(Collectors.joining(", ", "{ ", " }"));

            throw new RuntimeException("Exception while fetching all phones by params: "
                    + stringParams, e);
        }
    }
}
