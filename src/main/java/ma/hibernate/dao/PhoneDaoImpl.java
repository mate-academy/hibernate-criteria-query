package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.HibernateException;
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
            return phone;
        } catch (HibernateException e) {
            Objects.requireNonNull(transaction).rollback();
            throw new RuntimeException("Cant save phone: " + phone, e);
        } finally {
            Objects.requireNonNull(session).close();
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> item : params.entrySet()) {
                Predicate predicate = root.get(item.getKey()).in(item.getValue());
                predicates.add(predicate);
            }
            Predicate expression = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            query.where(expression);
            return session.createQuery(query).getResultList();
        } catch (HibernateException e) {
            throw new RuntimeException("Can`t get all phones with params: " + params, e);
        }
    }
}
