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
    private static final String FIELD_MAKER = "maker";
    private static final String FIELD_MODEL = "model";
    private static final String FIELD_OS = "os";
    private static final String FIELD_COLOR = "color";
    private static final String FIELD_COUNTRY = "countryManufactured";

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
            throw new RuntimeException("Can`t create new phone " + phone + ".Error " + e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            Predicate globalPredicate = null;
            for (String key : params.keySet()) {
                CriteriaBuilder.In<String> fieldPredicate =
                        criteriaBuilder.in(phoneRoot.get(key));
                for (String fieldValue : params.get(key)) {
                    fieldPredicate.value(fieldValue);
                }
                globalPredicate = globalPredicate == null
                        ? fieldPredicate : criteriaBuilder.and(globalPredicate, fieldPredicate);
            }

            return globalPredicate == null
                    ? session.createQuery("from Phone", Phone.class).getResultList()
                    : session.createQuery(query.where(globalPredicate)).getResultList();

        } catch (Exception e) {
            throw new RuntimeException("Can`t find all phones with params "
                    + params + ".Error " + e);
        }
    }
}
