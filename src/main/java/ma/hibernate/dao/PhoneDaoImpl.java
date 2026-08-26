package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Can't insert phone " + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> currentPredicate =
                        criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String value : entry.getValue()) {
                    currentPredicate.value(value);
                }
                predicates.add(currentPredicate);
            }
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();
        }
    }
}
