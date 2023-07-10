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
            throw new RuntimeException("can't create phone: " + phone);
        } finally {
            if (session != null) {
                session.close();
            }
            return phone;
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteria = builder.createQuery(Phone.class);
            Root<Phone> root = criteria.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            int i = 0;
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                Predicate predicate = root.get(entry.getKey()).in((Object[]) entry.getValue());
                predicates.add(predicate);
            }
            criteria.where(predicates.toArray(new Predicate[0]));
            return session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phone by parameters" + params, e);
        }
    }
}
