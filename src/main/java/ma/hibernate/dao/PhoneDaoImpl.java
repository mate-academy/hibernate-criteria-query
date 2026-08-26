package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaInPredicate;
import org.hibernate.query.criteria.JpaPredicate;
import org.hibernate.query.criteria.JpaRoot;

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
            throw new RuntimeException("Can't create phone: " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            JpaRoot<Phone> root = query.from(Phone.class);
            JpaPredicate[] jpaPredicates = new JpaPredicate[params.size()];
            int i = 0;
            for (Map.Entry<String, String[]> param : params.entrySet()) {
                JpaInPredicate<String> in = criteriaBuilder.in(root.get(param.getKey()),
                        param.getValue());
                jpaPredicates[i] = in;
                i++;
            }
            JpaPredicate and = criteriaBuilder.and(jpaPredicates);
            query.where(and);
            return session.createQuery(query)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Cannot get all phones");
        }
    }
}
