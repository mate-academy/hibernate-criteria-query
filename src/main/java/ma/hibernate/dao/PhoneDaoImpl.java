package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
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
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();

            // Проходимся по переданным параметрам и добавляем фильтры динамически
            params.forEach((key, value) -> predicates.add(root.get(key).in((Object[]) value)));

            criteriaQuery.where(predicates.toArray(new Predicate[0]));

            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
