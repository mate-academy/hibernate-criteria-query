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
        Transaction transaction = null;
        Session session = openSession();
        try {
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            transactionNullCheck(transaction);
            throw new RuntimeException("Could not persist a phone: " + phone, e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> root = cq.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String field = entry.getKey();
                String[] values = entry.getValue();

                predicates.add(root.get(field).in(Arrays.asList(values)));
            }

            cq.where(predicates.toArray(new Predicate[0]));

            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Could not find phones with params: " + params, e);
        }
    }

    private Session openSession() {
        return factory.openSession();
    }

    private void transactionNullCheck(Transaction transaction) {
        try {
            transaction.rollback();
        } catch (Exception e) {
            throw new RuntimeException("Could not rollback transaction: " + transaction, e);
        }
    }
}
