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
        Session currentSession = null;
        Transaction transaction = null;
        try {
            currentSession = super.factory.openSession();
            transaction = currentSession.beginTransaction();
            currentSession.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create comment: " + phone,e);
        } finally {
            currentSession.close();
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = super.factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> from = query.from(Phone.class);
            List<Predicate> list = new ArrayList<>();
            for (String key: params.keySet()) {
                CriteriaBuilder.In<String> in = criteriaBuilder.in(from.get(key));
                for (String param : params.get(key)) {
                    in.value(param);
                }
                Predicate and = criteriaBuilder.and(in);
                list.add(and);
            }
            Predicate and = criteriaBuilder.and(
                    list.toArray(list.toArray(new Predicate[list.size()])));
            query.where(and);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find",e);
        }

    }
}
