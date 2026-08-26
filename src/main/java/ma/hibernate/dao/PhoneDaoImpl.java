package ma.hibernate.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        EntityTransaction entityTransaction = null;

        try (EntityManager entityManager = this.factory.createEntityManager()) {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(phone);
            entityTransaction.commit();

            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can't create Phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (EntityManager entityManager = this.factory.createEntityManager()) {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
                String[] values = stringEntry.getValue();
                String key = stringEntry.getKey();

                CriteriaBuilder.In<String> in = builder.in(root.get(key));
                for (String value : values) {
                    in.value(value);
                }

                predicates.add(in);
            }

            query.select(root).where(predicates.toArray(new Predicate[0]));

            return entityManager
                    .createQuery(query)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find Phone with params. Params: " + params, e);
        }
    }
}
