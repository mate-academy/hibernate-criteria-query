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
            throw new RuntimeException("Can't create new phone" + phone, e);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);
            Predicate[] predicates = new Predicate[params.size()];
            int i = 0;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get(key));
                for (String value : entry.getValue()) {
                    in.value(value);
                }
                predicates[i] = criteriaBuilder.and(in);
                i++;
            }
            criteriaQuery.where(predicates);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
