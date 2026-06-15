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
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't add new phone: " + phone, e);
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
            CriteriaBuilder cb = factory.getCriteriaBuilder();
            CriteriaQuery<Phone> cbQuery = cb.createQuery(Phone.class);
            Root<Phone> root = cbQuery.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String key = param.getKey();
                String[] paramArr = params.get(key);
                CriteriaBuilder.In<String> paramPredicate = cb.in(root.get(key));
                for (String paramVal : paramArr) {
                    paramPredicate.value(paramVal);
                }
                predicates.add(paramPredicate);
            }
            cbQuery.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(cbQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cant find phones with such parameters: " + params, e);
        }
    }
}
