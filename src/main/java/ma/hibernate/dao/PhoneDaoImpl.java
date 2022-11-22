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
import org.hibernate.query.Query;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cQuery = cb.createQuery(Phone.class);
            Root<Phone> root = cQuery.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            params.forEach((param, values) -> {
                Predicate predicate = root.get(param).in((Object[]) values);
                predicates.add(predicate);
            });
            cQuery.select(root)
                    .where(predicates.toArray(new Predicate[]{}));
            Query<Phone> query = session.createQuery(cQuery);
            for (int i = 0; i < 2; i++) {
                i++;
            }
            for (int i = 0; i < 2; i++) {
                i++;
            }
            return query.getResultList();
        }
    }
}
