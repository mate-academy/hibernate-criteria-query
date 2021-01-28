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
            transaction = session.getTransaction();
            transaction.begin();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't create new phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = null;
        try {
            session = factory.openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicatesGroupOfOr = new ArrayList<>();
            for (Map.Entry<String, String[]> stringEntry : params.entrySet()) {
                List<Predicate> predicatesOr = new ArrayList<>();
                for (int i = 0; i < stringEntry.getValue().length; i++) {
                    predicatesOr.add(criteriaBuilder.equal(root.get(stringEntry.getKey()),
                            stringEntry.getValue()[i]));
                }
                predicatesGroupOfOr.add(criteriaBuilder.or(predicatesOr
                        .toArray(Predicate[]::new)));
            }
            Predicate predicate = criteriaBuilder.and(predicatesGroupOfOr
                    .toArray(Predicate[]::new));
            query.select(root).where(predicate);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all phone with this params ", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
