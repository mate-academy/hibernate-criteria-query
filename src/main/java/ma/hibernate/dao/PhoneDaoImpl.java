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
            session = PhoneDaoImpl.super.factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Cannot create a phone " + phone.getId(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        List<Phone> listOfPhones;
        List<Predicate> predicateList = new ArrayList<>();
        try (Session session = PhoneDaoImpl.super.factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> from = query.from(Phone.class);
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String key = entry.getKey();
                String[] val = params.get(key);

                boolean b = val.length > 1
                        ? predicateList.add(from.get(key).in((Object[]) val))
                        : predicateList.add(cb.equal(from.get(key), val[0]));
            }
            query.where(predicateList.toArray(Predicate[]::new));
            listOfPhones = session.createQuery(query).getResultList();
        }
        return listOfPhones;
    }
}
