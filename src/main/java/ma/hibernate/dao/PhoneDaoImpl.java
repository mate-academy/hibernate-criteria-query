package ma.hibernate.dao;

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
            throw new RuntimeException("Can't create new phone " + phone, e);
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
            CriteriaQuery query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate[] predicates = new Predicate[params.size()];
            CriteriaBuilder.In<String> predicate;
            int index = 0;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicate = criteriaBuilder.in(root.get(entry.getKey()));
                for (String parameter : entry.getValue()) {
                    predicate.value(parameter);
                }
                predicates[index++] = predicate;
            }
            query.where(predicates);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phones from given search request", e);
        }
    }
}
