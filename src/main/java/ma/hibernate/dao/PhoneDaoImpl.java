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
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone: " + phone, ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        String key = null;
        String[] value = null;
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> productPhone = criteriaQuery.from(Phone.class);
            List<Predicate> predicateList = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                CriteriaBuilder.In<Object> in = cb.in(productPhone.get(key));
                for (String par : value) {
                    in.value(par);
                }
                predicateList.add(in);
            }
            criteriaQuery.where(cb.and(predicateList
                    .toArray(predicateList.toArray(new Predicate[0]))));
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception ex) {
            throw new RuntimeException("Can't get from DB");
        }
    }
}
