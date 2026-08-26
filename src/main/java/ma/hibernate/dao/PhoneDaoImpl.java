package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
            session.save(phone);
        } catch (Exception e) {
            throw new RuntimeException("Can`t create a phone " + phone);
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        SessionFactory sessionFactory = factory.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                Path<Phone> phonePath = phoneRoot.get(entry.getKey());
                predicates.add(phonePath.in((Object[]) entry.getValue()));
            }
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        }
    }
}
