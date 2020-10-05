package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.exception.DataProcessingException;
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
            session = factory.openSession().getSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add phone with ID " + phone.getId(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession().getSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> findAllPhonesQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = findAllPhonesQuery.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> parameter : params.entrySet()) {
                predicates.add(root.get(parameter.getKey()).in(parameter.getValue()));
            }
            findAllPhonesQuery.select(root).where(predicates.toArray(Predicate[]::new));
            return session.createQuery(findAllPhonesQuery).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find all phones", e);
        }
    }
}
