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
            CriteriaQuery<Phone> phoneCriteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = phoneCriteriaQuery.from(Phone.class);
            Predicate phonePredicate = criteriaBuilder.conjunction();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> keyPredicate =
                        criteriaBuilder.in(phoneRoot.get(entry.getKey()));
                for (String param: entry.getValue()) {
                    keyPredicate.value(param);
                }
                phonePredicate = criteriaBuilder.and(phonePredicate, keyPredicate);
            }
            phoneCriteriaQuery.where(phonePredicate);
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                stringBuilder.append(entry.getKey()).append(" : ");
                for (String values : entry.getValue()) {
                    stringBuilder.append(values).append(" ");
                }
                stringBuilder.append("\n");
            }
            throw new RuntimeException("Can't find all phones with params: "
                    + stringBuilder.toString(), e);
        }
    }
}
