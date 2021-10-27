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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create Phone " + phone + " to DB");
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
            CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = criteriaQuery.from(Phone.class);

            CriteriaBuilder.In<String>[] predicates = new CriteriaBuilder.In[params.size()];
            int index = 0;
            for (Map.Entry<String, String[]> entry: params.entrySet()) {
                CriteriaBuilder.In<String> predicate =
                        cb.in(phoneRoot.get(entry.getKey()));
                for (String param : entry.getValue()) {
                    predicate.value(param);
                }
                predicates[index++] = predicate;
            }

            Predicate predicate = cb.and(predicates);
            criteriaQuery.where(predicate);
            return session.createQuery(criteriaQuery).getResultList();
        }
    }

    private void addValue(CriteriaBuilder.In<String> predicate, String[] criterias) {
        for (String criteria : criterias) {
            predicate.value(criteria);
        }
    }
}
