package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Could not add Phone to DB. " + phone, e);
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
            Root<Phone> root = query.from(Phone.class);
            CriteriaBuilder.In<String>[] predicates = new CriteriaBuilder.In[params.size()];
            int count = 0;
            for (Map.Entry<String, String[]> pair : params.entrySet()) {
                predicates[count] = criteriaBuilder.in(root.get(pair.getKey()));
                for (String value : pair.getValue()) {
                    predicates[count].value(value);
                }
                count++;
            }
            query.where(criteriaBuilder.and(predicates));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Could not get all Phones from DB.", e);
        }
    }
}
