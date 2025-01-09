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
import org.hibernate.query.Query;

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
            throw new RuntimeException("Can't insert phone", e);
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = builder.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);

            List<Predicate> predicates = new ArrayList<>();
            if (params.containsKey("countryManufactured")) {
                String[] countries = params.get("countryManufactured");
                predicates.add(root.get("countryManufactured").in((Object[]) countries));

            }
            if (params.containsKey("producer")) {
                String[] producers = params.get("producer");
                predicates.add(root.get("producer").in((Object[]) producers));
            }
            if (params.containsKey("color")) {
                String[] colors = params.get("color");
                predicates.add(root.get("color").in((Object[]) colors));
            }
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
            Query<Phone> query = session.createQuery(criteriaQuery);
            List<Phone> result = query.getResultList();
            session.close();
            return result;
        }
    }
}
