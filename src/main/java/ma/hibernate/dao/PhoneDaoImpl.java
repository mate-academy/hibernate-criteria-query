package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        Transaction transaction = null;

        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();

            session.persist(phone);

            transaction.commit();
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                String fieldName = getFieldName(entry.getKey());
                String[] values = entry.getValue();

                if (values == null || values.length == 0) {
                    continue;
                }

                predicates.add(root.get(fieldName).in((Object[]) values));
            }

            criteriaQuery.select(root);

            if (!predicates.isEmpty()) {
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
            }

            return session.createQuery(criteriaQuery).getResultList();
        }
    }
    private String getFieldName(String paramName) {
        if ("producer".equals(paramName)) {
            return "maker";
        }
        return paramName;
    }
}
