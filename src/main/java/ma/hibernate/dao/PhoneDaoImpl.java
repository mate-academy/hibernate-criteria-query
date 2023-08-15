package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ma.hibernate.dao.impl.EmptyParamsQueryStrategy;
import ma.hibernate.dao.impl.MakerAndColorAndCountryQueryStrategy;
import ma.hibernate.dao.impl.MakerAndColorQueryStrategy;
import ma.hibernate.dao.impl.MakerQueryStrategy;
import ma.hibernate.dao.impl.ModelQueryStrategy;
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
            session.save(phone);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save phone to DB: " + phone, e);
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
            List<String> paramsDb = new ArrayList<>();

            for (Map.Entry<String, String[]> entrySet : params.entrySet()) {
                paramsDb.add(entrySet.getKey());
            }

            Map<List<String>, QueryStrategy> strategyMap = new HashMap<>();
            strategyMap.put(List.of("color", "countryManufactured", "maker"),
                    new MakerAndColorAndCountryQueryStrategy());
            strategyMap.put(List.of("color", "maker"), new MakerAndColorQueryStrategy());
            strategyMap.put(List.of("maker"), new MakerQueryStrategy());
            strategyMap.put(List.of("model"), new ModelQueryStrategy());
            strategyMap.put(Collections.emptyList(), new EmptyParamsQueryStrategy());

            QueryStrategy queryStrategy = strategyMap.get(paramsDb);
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> query = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> phoneRoot = query.from(Phone.class);
            return queryStrategy.findAll(session, criteriaBuilder, query, phoneRoot, params);
        }
    }
}
