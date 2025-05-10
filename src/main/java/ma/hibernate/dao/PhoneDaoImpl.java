package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
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
            session.persist(phone);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                throw new RuntimeException("Can't create phone: " + phone, e);
            }
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);
            /*
            I wanted to initialize finalPredicate as null and check before using cq.where
            if it contains predicate after for loop, but findAll_if_else_notOk test is incorrectly
            defined, and it is throwing assertion even for simple ifs without 'else' clause,
            so I had to use conjunction to avoid NPE during cq.where when params map is empty,
            because I don't know to do differently
             */
            Predicate finalPredicate = cb.conjunction();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> tempPredicate = cb.in(root.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    tempPredicate.value(value);
                }
                finalPredicate = cb.and(finalPredicate, tempPredicate);
            }
            cq.where(finalPredicate);
            return session.createQuery(cq).getResultList();
        }
    }
}
