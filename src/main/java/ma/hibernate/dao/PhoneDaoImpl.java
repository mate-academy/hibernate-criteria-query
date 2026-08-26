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

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.persist(phone);
            session.getTransaction().commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can't create phone : " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);
            List<Predicate> listOfPredicates = new ArrayList<>();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String key = param.getKey();
                String[] value = param.getValue();
                listOfPredicates.add(phoneRoot.get(key).in(Arrays.stream(value).toList()));
            }

            criteriaQuery.where(cb.and(listOfPredicates.toArray(new Predicate[0])));
            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
