package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ma.hibernate.exception.DataProcessingException;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.*;

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
            throw new DataProcessingException("Can't insert phone " + phone, e);
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
            Root<Phone> phoneRoot = query.from(Phone.class);
            List<CriteriaBuilder.In<String>> criteraBuilderList = new ArrayList<>();
            for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
                CriteriaBuilder.In<String> predicate = cb.in(phoneRoot.get(stringEntry.getKey()));
                for (String value : stringEntry.getValue()) {
                    predicate.value(value);
                }
                criteraBuilderList.add(predicate);
            }
            Predicate predicate = cb.and();
            for (CriteriaBuilder.In<String> stringIn : criteraBuilderList) {
                predicate = cb.and(stringIn, predicate);
            }
            query = query.where(predicate);
            return session.createQuery(query).getResultList();
        }
    }
}
