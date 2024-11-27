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
        Transaction tx = null;
        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();
            session.persist(phone);
            tx.commit();
            return phone;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException("Create phone failed", e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> cq = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = cq.from(Phone.class);
            List<Predicate> predicateList = new ArrayList<>();

            for (Map.Entry<String, String[]> param : params.entrySet()) {
                String key = param.getKey();
                String[] values = param.getValue();
                CriteriaBuilder.In<String> inClause = cb.in(phoneRoot.get(key));
                for (String value : values) {
                    inClause.value(value);
                }
                predicateList.add(inClause);
                Predicate finalPredicate = cb.and(predicateList.toArray(new Predicate[0]));
                cq.where(finalPredicate);
            }
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Find phones failed", e);
        }
    }
}
