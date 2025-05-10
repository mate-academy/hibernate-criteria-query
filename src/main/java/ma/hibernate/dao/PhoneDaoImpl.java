package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
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
            throw new RuntimeException("Can't insert phone session from DB "
                    + phone, e);
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
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<CriteriaBuilder.In<String>> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry: params.entrySet()) {
                CriteriaBuilder.In<String> predicate = cb.in(root.get(entry.getKey()));
                Stream.of(entry.getValue()).forEach(predicate::value);
                predicates.add(predicate);
            }
            query.where(cb.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(query).list();
        }
    }
}
