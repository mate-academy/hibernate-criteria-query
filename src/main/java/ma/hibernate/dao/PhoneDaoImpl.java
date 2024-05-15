package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    private final static String MODEL = "model";
    private final static String MAKER = "maker";
    private final static String COLOR = "color";
    private final static String OS = "os";
    private final static String COUNTRYMANUFACTURED = "countryManufactured";

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
            throw new RuntimeException("Cannnot create phone: " + phone.toString(), e);
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);
            Predicate mainPredicate = cb.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> in = cb.in(root.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    in.value(value);
                }
                mainPredicate = cb.and(mainPredicate, in);
            }
            cq.where(mainPredicate);
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot find all phones", e);
        }
    }
}
