package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = factory.openSession();
        session.beginTransaction();
        session.save(phone);
        session.getTransaction().commit();
        session.close();
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = builder.createQuery(Phone.class);
        Root<Phone> root = query.from(Phone.class);

        Predicate[] predicates = params.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    String[] values = entry.getValue();
                    Path<String> attribute = root.get(key);
                    return attribute.in((Object[]) values);
                })
                .toArray(Predicate[]::new);

        query.select(root).where(predicates);
        Query<Phone> criteriaQuery = session.createQuery(query);
        List<Phone> phones = criteriaQuery.getResultList();
        session.close();
        return phones;
    }
}
