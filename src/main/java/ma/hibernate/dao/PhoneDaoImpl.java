package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            session.persist(phone);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create phone: " + phone, e);
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
            final CriteriaBuilder cb = session.getCriteriaBuilder();
            final CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            final Root<Phone> root = criteriaQuery.from(Phone.class);
            // WHERE countryManufactured IN (China)
            //      AND maker IN (apple, nokia, samsung)
            //      AND color IN (white, red)
            List<Predicate> listOfPredicates = new ArrayList<>();
            params.forEach((fieldName, fieldValuesArray) -> {
                final CriteriaBuilder.In<String> inPredicate = cb.in(root.get(fieldName));
                for (final String fieldValue : fieldValuesArray) {
                    inPredicate.value(fieldValue);
                }
                listOfPredicates.add(inPredicate);
            });

            Predicate fullQueryPredicate = cb.and(listOfPredicates.toArray(new Predicate[0]));
            criteriaQuery.where(fullQueryPredicate);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }
}
