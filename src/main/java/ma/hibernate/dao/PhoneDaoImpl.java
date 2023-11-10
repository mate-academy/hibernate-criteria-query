package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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
            CriteriaBuilder.In<String> maker = cb.in(root.get("maker"));
            CriteriaBuilder.In<String> model = cb.in(root.get("model"));
            CriteriaBuilder.In<String> color = cb.in(root.get("color"));
            CriteriaBuilder.In<String> countryManufactured = cb.in(root.get("countryManufactured"));
            for (Map.Entry<String,String[]> mapsElem : params.entrySet()) {
                for (String element: mapsElem.getValue()) {
                    model.value(element);
                    maker.value(element);
                    color.value(element);
                    countryManufactured.value(element);
                }
            }
            CriteriaQuery<Phone> querys = params.isEmpty() ? query.where() :
                    params.containsKey("countryManufactured")
                            && params.containsKey("color")
                            ? query.where(cb.and(maker,color,countryManufactured)) :
                    params.containsKey("color") ? query.where(cb.and(maker,color)) :
                            query.where(cb.or(model,maker));
            return session.createQuery(querys).getResultList();
        }
    }
}
