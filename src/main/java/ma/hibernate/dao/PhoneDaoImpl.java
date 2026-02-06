package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(phone);
        session.getTransaction().commit();
        session.close();
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = getSessionFactory().openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
        Root<Phone> root = query.from(Phone.class);

        List<Predicate> predicates = new ArrayList<>();

        params.forEach((key, values) ->
                predicates.add(root.get(key).in((Object[]) values))
        );

        query.where(predicates.toArray(new Predicate[0]));

        List<Phone> result = session.createQuery(query).getResultList();
        session.close();
        return result;
    }
}
