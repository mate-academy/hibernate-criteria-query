package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
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
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Phone wasn't saved DB" + phone + " Rollback ", e);
        } finally {
            if (session != null) {
                System.out.println("Closing session");
                session.close();
            } else {
                System.out.println("Warning. Session was not opened");
            }
        }
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        // SELECT * FROM phones AS p
        // WHERE p.color IN colors
        // AND p.maker IN makers
        // AND p.countryManufactured IN countries

        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            List<Predicate> predicatesToCombineUsingAnd = new ArrayList<>();
            predicatesToCombineUsingAnd.add(cb.isTrue(cb.literal(true)));

            for (var e : params.entrySet()) {
                String key = e.getKey();
                // Expression helps us to filter by fields and set special conditions
                Expression<String> expression = root.get(key);
                Predicate predicate = expression.in(List.of(params.get(key)));
                predicatesToCombineUsingAnd.add(predicate);
            }
            // From list of predicates to array of predicates for builder and method
            query.where(cb.and(predicatesToCombineUsingAnd.toArray(new Predicate[0])));
            return session.createQuery(query).getResultList();

            // Map<String, String[]> allCriteria = Map.of(
            //     "countryManufactured", new String[] {"USA", "Korea", "China"},
            //     "color", new String[]{"red", "white", "black"},
            //     "maker", new String[]{"Apple", "Samsung", "Oppo", "Xiaomi"});

            // List<Phone> all = getAll();
            // // query.select(query.from(Phone.class)).where(colorExpression.in(countries));
            // query.select(query.from(Phone.class)).where(colorExpression.in(countries));
            // CriteriaQuery<Phone> criteriaQuery = cb.createQuery(Phone.class);
            // Root<Phone> root = criteriaQuery.from(Phone.class);
            // CriteriaBuilder.In<String> inClause = cb.in(root.get("color"));
            // for (String color : colors) {
            //    inClause.value(color);
            // }
            // criteriaQuery.select(root).where(inClause);
            // List<Phone> resultList = session.createQuery(criteriaQuery).getResultList();
        }
    }

    private List<Phone> getAll() {
        try (Session session = factory.openSession()) {
            Query<Phone> getAllSmilesQuery = session.createQuery("FROM Phone", Phone.class);
            return getAllSmilesQuery.list();
        } catch (Exception e) {
            throw new RuntimeException("Can't get all phones", e);
        }
    }
}
