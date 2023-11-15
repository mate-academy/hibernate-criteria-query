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
        Session session = null;
        Transaction transaction = null;

        try {
            session = factory.openSession();
            transaction = session.beginTransaction();

            session.persist(phone);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new RuntimeException("Can't insert a phone " + phone, e);
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
            CriteriaQuery<Phone> getPhonesByFiltersQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = getPhonesByFiltersQuery.from(Phone.class);

            CriteriaBuilder.In<String> paramPredicate;
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, String[]> filterParam : params.entrySet()) {
                paramPredicate = criteriaBuilder.in(phoneRoot.get(filterParam.getKey()));

                for (String value : filterParam.getValue()) {
                    paramPredicate.value(value);
                }

                predicates.add(paramPredicate);
            }

            getPhonesByFiltersQuery.where(predicates.toArray(new Predicate[]{}));
            return session.createQuery(getPhonesByFiltersQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get phones from DB "
                     + "with following params " + params, e);
        }
    }
}
