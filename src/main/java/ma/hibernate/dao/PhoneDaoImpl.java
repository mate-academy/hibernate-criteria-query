package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao<Phone> implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        return super.create(phone);
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> phoneCriteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = phoneCriteriaQuery.from(Phone.class);
            List<Predicate> predicates = new ArrayList<>();
            params.forEach((key, value) -> predicates.add(root.get(key).in(Arrays.asList(value))));
            phoneCriteriaQuery.select(root).where(predicates.toArray(new Predicate[]{}));
            return session.createQuery(phoneCriteriaQuery).getResultList();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to get all phones by such parameters", exception);
        }
    }
}
