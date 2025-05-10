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
    private Session session;

    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error while creating phone: " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicate = new ArrayList<>();
            String[] countryManufacturedValues = params.get("countryManufactured");
            String[] producerValues = params.get("maker");
            String[] colorValues = params.get("color");
            String[] modelValues = params.get("model");
            predicate.add(
                    countryManufacturedValues != null && countryManufacturedValues.length > 0
                            ? root.get("countryManufactured")
                            .in((Object[]) countryManufacturedValues)
                            : criteriaBuilder.and()
            );
            predicate.add(
                    producerValues != null && producerValues.length > 0
                            ? root.get("maker").in((Object[]) producerValues)
                            : criteriaBuilder.and()
            );
            predicate.add(
                    colorValues != null && colorValues.length > 0
                            ? root.get("color").in((Object[]) colorValues)
                            : criteriaBuilder.and()
            );
            predicate.add(
                    modelValues != null && modelValues.length > 0
                            ? root.get("model").in((Object[]) modelValues)
                            : criteriaBuilder.and()
            );
            Predicate finalPredicate = criteriaBuilder
                    .and(predicate.toArray(predicate.toArray(new Predicate[0])));
            query.where(finalPredicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error while finding phones", e);
        }
    }
}
