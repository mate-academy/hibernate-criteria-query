package ma.hibernate.dao;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can`t create Phone = " + phone,e);
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            for (Map.Entry<String,String[]> mapsElem : params.entrySet()) {
                for (String element: mapsElem.getValue()) {
                    System.out.println(element);
                    Predicate maker = cb.equal(root.get("maker"), element);
                    Predicate countryManufactured = cb.equal(root.get("countryManufactured"), element);
                    Predicate color = cb.equal(root.get("color"), element);
                    Predicate alles = cb.and(maker, color, countryManufactured);
                }
            }

            List<Phone> resultList = session.createQuery(query).getResultList();
            System.out.println(resultList);
            return resultList;
        }
    }
}
