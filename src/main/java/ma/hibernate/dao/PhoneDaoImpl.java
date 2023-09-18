package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
            Predicate predicate = criteriaBuilder.conjunction();

            String[] countryManufacturedValues = params.get("countryManufactured");
            String[] producerValues = params.get("producer");
            String[] colorValues = params.get("color");

            if (countryManufacturedValues != null && countryManufacturedValues.length > 0) {
                predicate = criteriaBuilder.and(predicate,
                        root.get("countryManufactured").in((Object[]) countryManufacturedValues));
            }

            if (producerValues != null && producerValues.length > 0) {
                predicate = criteriaBuilder.and(predicate,
                        root.get("producer").in((Object[]) producerValues));
            }

            if (colorValues != null && colorValues.length > 0) {
                predicate = criteriaBuilder.and(predicate,
                        root.get("color").in((Object[]) colorValues));
            }

            query.select(root); // Вибрати всі поля екземплярів Phone
            query.where(predicate);

            List<Phone> phones = session.createQuery(query).getResultList();

            transaction.commit();
            return phones;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error while finding phones", e);
        }
    }
}
