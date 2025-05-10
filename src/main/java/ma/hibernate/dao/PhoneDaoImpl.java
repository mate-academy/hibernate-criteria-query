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
            throw new RuntimeException("Can't create phone " + phone, e);
        } finally {
            if (session != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneQuery.from(Phone.class);
            List<Predicate> predicateList = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                predicateList.add(phoneRoot.get(entry.getKey()).in((Object[]) entry.getValue()));
            }
            phoneQuery.select(phoneRoot).where(predicateList.toArray(new Predicate[0]));
            return session.createQuery(phoneQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all phones with this parameters: " + params, e);
        }
    }
}
