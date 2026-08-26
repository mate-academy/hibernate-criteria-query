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
            throw new RuntimeException("Can't create the phone: " + phone, e);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery findAllWithFilteringQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllWithFilteringQuery.from(Phone.class);
            Predicate paramsAnd = criteriaBuilder.conjunction();
            for (Map.Entry entry: params.entrySet()) {
                CriteriaBuilder.In<String> paramsIn =
                        criteriaBuilder.in(phoneRoot.get((String) entry.getKey()));
                for (String value: (String[]) entry.getValue()) {
                    paramsIn.value(value);
                }
                paramsAnd = criteriaBuilder.and(paramsAnd, paramsIn);
            }
            findAllWithFilteringQuery.where(paramsAnd);
            return session.createQuery(findAllWithFilteringQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all Phones with params: " + params, e);
        }
    }
}
