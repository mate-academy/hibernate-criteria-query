package ma.hibernate.dao;

import java.util.LinkedList;
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
            session = this.factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert a phone: " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = this.factory.openSession()) {
            if (params.isEmpty()) {
                Query<Phone> query = session.createQuery("from Phone", Phone.class);
                return query.getResultList();
            }

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            CriteriaBuilder.In<String> predicate = null;
            List<Predicate> predicateList = new LinkedList<>();
            for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
                predicate = cb.in(phoneRoot.get(stringEntry.getKey()));
                for (String chosenParams : stringEntry.getValue()) {
                    predicate.value(chosenParams);
                }
                predicateList.add(predicate);
            }
            Predicate[] predicateArray = new Predicate[predicateList.size()];
            query.where(cb.and(predicateList.toArray(predicateArray)));
            return session.createQuery(query).getResultList();
        }
    }
}
