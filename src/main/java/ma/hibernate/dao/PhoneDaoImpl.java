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
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Cannot create a phone: " + phone + " to DB", e);
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
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            CriteriaBuilder.In<String>[] inArray = new CriteriaBuilder.In[params.keySet().size()];
            int index = 0;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                inArray[index] = cb.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    inArray[index].value(value);
                }
                index++;
            }
            query.where(cb.and(inArray));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot find all phones from DB", e);
        }
    }
}
