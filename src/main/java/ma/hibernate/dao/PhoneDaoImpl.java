package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        Transaction tx = null;
        try {
            session = factory.openSession();
            tx = session.beginTransaction();
            session.save(phone);
            tx.commit();
            return phone;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException("Can't save the phone to db: " + phone,e);
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
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder
                    .createQuery(Phone.class);
            Root<Phone> root = criteriaQuery
                    .from(Phone.class);
            CriteriaBuilder.In<String> predicateParam = null;
            List<Predicate> predicates = new ArrayList<Predicate>();
            Set<Map.Entry<String, String[]>> entries = params.entrySet();
            for (Map.Entry<String, String[]> entry : entries) {
                predicateParam = criteriaBuilder.in(root.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    predicateParam.value(value);
                }
                predicates.add(predicateParam);
            }
            criteriaQuery.where(criteriaBuilder
                    .and(predicates
                            .toArray(new Predicate[predicates.size()])));
            return session.createQuery(criteriaQuery).getResultList();
        }

    }
}
