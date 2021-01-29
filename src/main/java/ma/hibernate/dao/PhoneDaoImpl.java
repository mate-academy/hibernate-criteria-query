package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
        Session session = null;
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
            throw new RuntimeException("can't create phone" + phone, e);
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
            CriteriaQuery<Phone> findAll = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = findAll.from(Phone.class);
            List<Predicate> andParams = new ArrayList<>();
            for (Map.Entry<String,String[]> entry : params.entrySet()) {
                List<Predicate> orParams = new ArrayList<>();
                for (String parameter : entry.getValue()) {
                    orParams.add(criteriaBuilder.equal(root.get(entry.getKey()),parameter));
                }
                andParams.add(criteriaBuilder.or(orParams.toArray(new Predicate[]{})));
            }
            Predicate predicate = criteriaBuilder.and(andParams.toArray(new Predicate[]{}));
            findAll.select(root).where(predicate);
            return session.createQuery(findAll).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find your request", e);
        }
    }
}
