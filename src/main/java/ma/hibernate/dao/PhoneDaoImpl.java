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
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone " + phone, e);
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
            CriteriaQuery<Phone> phoneQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneQuery.from(Phone.class);
            Predicate mainPredicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                Path<String> entityFieldsPath = phoneRoot.get(entry.getKey());
                CriteriaBuilder.In<String> keyPredicate = cb.in(entityFieldsPath);
                for (String element : entry.getValue()) {
                    keyPredicate.value(element);
                }
                mainPredicate = cb.and(mainPredicate, keyPredicate);
            }
            phoneQuery.where(mainPredicate);
            return session.createQuery(phoneQuery).getResultList();
        }
    }
}
