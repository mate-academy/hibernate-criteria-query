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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add phone:" + phone, e);
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
            List<Predicate> predicates = new ArrayList<>();

            if (params.containsKey("countryManufactured")) {
                String[] countries = params.get("countryManufactured");
                if (countries.length > 0) {
                    predicates.add(phoneRoot.get("countryManufactured").in((Object[]) countries));
                }
            }

            if (params.containsKey("countryManufactured")) {
                String[] makers = params.get("maker");
                if (makers.length > 0) {
                    predicates.add(phoneRoot.get("maker").in((Object[]) makers));
                }
            }

            if (params.containsKey("color")) {
                String[] colors = params.get("color");
                if (colors.length > 0) {
                    predicates.add(phoneRoot.get("color").in((Object[]) colors));
                }
            }
            query.select(phoneRoot).where(cb.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        }
    }
}
