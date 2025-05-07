package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Could not create the phone: " + phone);
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

            CriteriaBuilder.In<String> countryPredicate
                    = cb.in(phoneRoot.get("countryManufactured"));
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                countryPredicate.value(String.valueOf(entry));
            }

            CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                makerPredicate.value(String.valueOf(entry));
            }

            CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                colorPredicate.value(String.valueOf(entry));
            }

            query.where(cb.and(countryPredicate, makerPredicate, colorPredicate));
            return session.createQuery(query).getResultList();
        }
    }
}




