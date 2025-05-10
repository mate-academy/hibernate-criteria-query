package ma.hibernate.dao;

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
        Session session = null;
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
            throw new RuntimeException("Can't add phone: " + phone + " to DB!", e);
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
            CriteriaBuilder crtBld = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = crtBld.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            String[] keySet = params.keySet().toArray(new String[0]);
            Predicate[] predicates = new Predicate[keySet.length];
            for (int i = 0; i < predicates.length; i++) {
                CriteriaBuilder.In<String> predicate = crtBld.in(phoneRoot.get(keySet[i]));
                for (String str: params.get(keySet[i])) {
                    predicate.value(str);
                }
                predicates[i] = predicate;
            }
            query.where(crtBld.and(predicates));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get a list of phones matching the request!", e);
        }
    }
}
