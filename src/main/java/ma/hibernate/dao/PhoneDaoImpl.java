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
            session.save(phone);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Can not save Phone to DB. Phone: " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> rootClass = query.from(Phone.class);

            Predicate[] predicateList = new Predicate[params.size()];
            int count = 0;

            for (Map.Entry<String, String[]> record: params.entrySet()) {
                CriteriaBuilder.In<String> inPredicate =
                        criteriaBuilder.in(rootClass.get(record.getKey()));
                for (String value : record.getValue()) {
                    inPredicate.value(value);
                }
                predicateList[count++] = inPredicate;
            }

            query.where(predicateList);

            return session.createQuery(query).getResultList();
        } catch (Exception e) {

            throw new RuntimeException("Can not get list of Phones by params. Params: "
                    + params, e);
        }
    }
}
