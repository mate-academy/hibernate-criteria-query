package ma.hibernate.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
            throw new RuntimeException("Can`t create Phone = " + phone,e);
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> root = query.from(Phone.class);
            Predicate [] predicate = new Predicate[3];
            CriteriaBuilder.In<String> maker = cb.in(root.get("maker"));
            CriteriaBuilder.In<String> model = cb.in(root.get("model"));
            CriteriaBuilder.In<String> color = cb.in(root.get("color"));
            CriteriaBuilder.In<Object> countryManufactured = cb.in(root.get("countryManufactured"));
            for (Map.Entry<String,String[]> mapsElem : params.entrySet()) {
                for (String element: mapsElem.getValue()) {
                    countryManufactured.value(element);
                    model.value(element);
                    maker.value(element);
                    color.value(element);
                    predicate[0] = cb.equal(root.get("model"), element);
                }
            }
          //  predicate[2] = cb.and(maker,model);
            predicate[1] = cb.and(maker,color,countryManufactured);
            query.where(cb.or(model,predicate[1]));
            List<Phone> resultList = session.createQuery(query).getResultList();
            System.out.println(resultList);
            return resultList;
        }
    }
}
