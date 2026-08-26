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
    private static final String CAN_NOT_CREATE_NEW_PHONE = "Can't create a new phone: ";
    private static final String CAN_NOT_FINE_All_PHONES = "Can't find all the phones.";

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
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(CAN_NOT_CREATE_NEW_PHONE + phone, e);
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
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicateList = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                CriteriaBuilder.In<String> stringIn = criteriaBuilder.in(root.get(entry.getKey()));
                for (String par : entry.getValue()) {
                    stringIn.value(par);
                }
                predicateList.add(stringIn);
            }
            query.where((criteriaBuilder.and(predicateList.toArray(new Predicate[0]))));
            return session.createQuery(query).getResultList();

        } catch (RuntimeException e) {
            throw new RuntimeException(CAN_NOT_FINE_All_PHONES, e);
        }

    }
}
