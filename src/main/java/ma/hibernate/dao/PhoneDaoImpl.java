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
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Cant insert phone in db"
                    + phone, e);
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
            CriteriaQuery<Phone> getAllPhonesQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = getAllPhonesQuery.from(Phone.class);
            Predicate predicate = criteriaBuilder.and();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> parametersRange
                        = criteriaBuilder.in(root.get(entry.getKey()));
                for (String parameter : entry.getValue()) {
                    parametersRange.value(parameter);
                }
                predicate = criteriaBuilder.and(predicate, parametersRange);
            }
            getAllPhonesQuery.select(root).where(predicate);
            return session.createQuery(getAllPhonesQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't gael phones from DB", e);
        }
    }
}
