package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.HibernateError;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        factory.inTransaction(session -> session.persist(phone));
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = cb.createQuery(Phone.class);
            Root<Phone> from = query.from(Phone.class);
            List<Predicate> resultList = new ArrayList<>();
            for (Map.Entry<String, String[]> inputMap : params.entrySet()) {
                CriteriaBuilder.In<String> in = cb.in(from.get(inputMap.getKey()));
                for (String string : inputMap.getValue()) {
                    in.value(string);
                }
                resultList.add(in);
            }
            Predicate[] array = resultList.toArray(Predicate[]::new);
            query.where(cb.and(array));
            return session.createQuery(query).getResultList();
        } catch (HibernateError e) {
            throw new RuntimeException();
        }
    }
}
