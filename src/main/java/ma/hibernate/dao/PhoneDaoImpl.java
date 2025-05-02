package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
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
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can`t save phone " + phone);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    //SELECT * FROM PHONES WHERE country_manufactured = 'China'
    //  AND producer IN ('apple', 'nokia', 'samsung')
    //  AND color IN ('white', 'red');

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (String key : params.keySet()) {
                CriteriaBuilder.In<Object> predicate = cb.in(phoneRoot.get(key));
                for (String type : params.get(key)) {
                    predicate.value(type);
                }
                predicates.add(predicate);
            }
            query.where(cb.and(predicates.toArray(predicates.toArray(new Predicate[0]))));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Could not get all phones with params: "
                    + params.entrySet());
        }
    }
}
