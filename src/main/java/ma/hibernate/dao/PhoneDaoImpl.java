package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        try {
            factory.inTransaction(session -> session.persist(phone));
            return phone;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add new phone to the DB," + phone, e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            Predicate[] predicates = params.entrySet().stream()
                    .map(e -> phoneRoot.get(e.getKey()).in(e.getValue()))
                    .toArray(Predicate[]::new);
            query.where(predicates);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find phones according to your parameters: "
                    + mapToString(params));
        }
    }

    private static String mapToString(Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(String.join(",", entry.getValue()))
                    .append("; ");
        }
        sb.delete(sb.length() - 2, sb.length()).append("}");
        return sb.toString();
    }
}
