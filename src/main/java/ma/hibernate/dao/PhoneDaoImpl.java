package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {

    public static final String ERROR_DURING_CREATION_PHONE =
            "Error during creation Phone -> %s";
    public static final String ERROR_DURING_RETRIEVING_PHONES_WITH_SUCH_PARAMS =
            "Error during retrieving phones with such params -> %s";

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
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(ERROR_DURING_CREATION_PHONE.formatted(phone), e);
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
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);
            List<Predicate> predicates = new LinkedList<>();
            for (Entry<String, String[]> entry : params.entrySet()) {
                Path<String> param = root.get(entry.getKey());
                Predicate pr = param.in(Arrays.asList(entry.getValue()));
                predicates.add(pr);
            }
            cq.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException(
                    ERROR_DURING_RETRIEVING_PHONES_WITH_SUCH_PARAMS.formatted(params), e);
        }
    }
}
