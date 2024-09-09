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
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(phone);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to create a phone = [" + phone + "]");
        } finally {
            session.close();
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        Session session = factory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
        Root<Phone> root = query.from(Phone.class);
        List<CriteriaBuilder.In<String>> inExpressions = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get(entry.getKey()));
            inExpressions.add(in);
            for (String param : entry.getValue()) {
                in.value(param);
            }
        }
        Predicate predicate = criteriaBuilder.and(inExpressions.toArray(Predicate[]::new));
        query.where(predicate);
        return session.createQuery(query).getResultList();
    }
}
