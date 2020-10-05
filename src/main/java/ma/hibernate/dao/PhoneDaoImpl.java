package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.log4j.Log4j;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Log4j
public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        log.info("Calling a create() method of PhoneDaoImpl class");
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(phone);
            log.info("Attempt to save phone " + phone + " in db.");
            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't insert phone entity", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        log.info("Calling a findAll() method of PhoneDaoImpl class");
        Session session = null;
        try {
            List<Predicate> predicates = new ArrayList<>();
            session = factory.openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            for (Map.Entry<String, String[]> set: params.entrySet()) {
                predicates.add(root.get(set.getKey()).in(set.getValue()));
            }
            query.select(root).where(predicates.toArray(new Predicate[]{}));
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
