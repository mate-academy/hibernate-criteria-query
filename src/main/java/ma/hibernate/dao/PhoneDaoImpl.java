package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            transaction.rollback();
            throw new RuntimeException("Couldn't create a phone " + phone, e);
        } finally {
            session.close();
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
        Root<Phone> phoneRoot = query.from(Phone.class);
        ArrayList<CriteriaBuilder.In<String>> predicates = new ArrayList<>();
        for (String paramName : params.keySet()) {
            CriteriaBuilder.In<String> iterPredicate = criteriaBuilder.in(phoneRoot.get(paramName));
            for (String param : params.get(paramName)) {
                iterPredicate.value(param);
            }
            predicates.add(iterPredicate);
        }
        Predicate finalPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        query.where(finalPredicate);
        return session.createQuery(query).getResultList();
    }
}
