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
        Transaction transaction = null;
        Session session = null;
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
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> findAllPhonesWithParametersQuery =
                    criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = findAllPhonesWithParametersQuery.from(Phone.class);
            Predicate mainPredicate = criteriaBuilder.and();
            for (Map.Entry<String, String[]> parameters : params.entrySet()) {
                CriteriaBuilder.In<String> parameterPredicate =
                        criteriaBuilder.in(phoneRoot.get(parameters.getKey()));
                for (String parametr : parameters.getValue()) {
                    parameterPredicate.value(parametr);
                }
                mainPredicate = criteriaBuilder.and(mainPredicate, parameterPredicate);
            }
            findAllPhonesWithParametersQuery.where(mainPredicate);
            return session.createQuery(findAllPhonesWithParametersQuery).getResultList();
        }
    }
}
