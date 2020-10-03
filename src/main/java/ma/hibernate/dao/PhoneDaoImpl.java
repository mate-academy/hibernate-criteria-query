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

@Log4j
public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        log.info("Calling a create() method of PhoneDaoImpl class");
        Session session = null;
        try {
            session = factory.openSession();
            session.beginTransaction();
            session.save(phone);
            log.info("Attempt to save phone " + phone + " in db.");
            session.getTransaction().commit();
            return phone;
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
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
            List<CriteriaBuilder.In<String>> predicates = new ArrayList<>();
            session = factory.openSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            for (Map.Entry<String, String[]> set: params.entrySet()) {
                CriteriaBuilder.In<String> tempPredicate = cb.in(root.get(set.getKey()));
                for (String str : set.getValue()) {
                    tempPredicate.value(str);
                }
                predicates.add(tempPredicate);
            }
            query.select(root).where(cb.and(cb.and(predicates.toArray(new Predicate[]{}))));
            List<List<Phone>> phones = new ArrayList<>();
            phones.add(session.createQuery(query).getResultList());
            List<Phone> result = new ArrayList<>();
            for (List<Phone> list : phones) {
                result.addAll(list);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
