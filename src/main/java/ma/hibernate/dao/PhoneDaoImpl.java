package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
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
            throw new RuntimeException("Can't create phone: " + phone.getModel(), e);
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
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String field = entry.getKey();
                String[] values = entry.getValue();

                predicates.add(root.get(field).in(Arrays.asList(values)));
            }

            query.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(query).getResultList();
        }
    }

    // далі додаткові методи для власної практики та розуміння теми
    public List<Phone> findAllPhones() {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            query.select(root);
            return session.createQuery(query).getResultList();
        }
    }

    public List<Phone> findByMakers(String[] makers) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate predicate = root.get("maker").in(Arrays.asList(makers));
            query.where(predicate);
            return session.createQuery(query).getResultList();
        }
    }

    public List<Phone> findByCountryAndColor(String country, String[] colors) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate countryPredicate = cb.equal(root.get("countryManufactured"), country);
            Predicate colorPredicate = root.get("color").in(Arrays.asList(colors));
            Predicate and = cb.and(colorPredicate, countryPredicate);
            query.where(and);
            return session.createQuery(query).getResultList();
        }
    }
}
