package ma.hibernate.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import ma.hibernate.exception.DataProcessingException;
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
        } catch (RuntimeException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Phone not added to db. " + phone);
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
            String[] modelList = params.get("model")
                    == null ? new String[0] : params.get("model");
            Predicate model = modelList.length == 0
                    ? null : root.get("model").in((Object[]) modelList);
            String[] countryManufactureds = params.get("countryManufactured")
                    == null ? new String[0] : params.get("countryManufactured");
            Predicate equal1 = countryManufactureds.length == 0
                    ? null : root.get("countryManufactured").in((Object[]) countryManufactureds);
            String[] makers = params.get("maker") == null ? new String[0] : params.get("maker");
            CriteriaBuilder.In<Object> inMaker = makers.length
                    == 0 ? null : cb.in(root.get("maker"));
            for (String maker : makers) {
                inMaker.value(maker);
            }
            String[] colors = params.get("color") == null ? new String[0] : params.get("color");
            CriteriaBuilder.In<Object> inColor = colors.length
                    == 0 ? null : cb.in(root.get("color"));
            for (String color : colors) {
                inColor.value(color);
            }
            Predicate finalPredicate = cb.and(
                    Stream.of(equal1, inMaker, inColor, model)
                            .filter(Objects::nonNull)
                            .toArray(Predicate[]::new)
            );
            query.where(finalPredicate);
            return session.createQuery(query).getResultList();
        } catch (RuntimeException e) {
            throw new DataProcessingException("Phone not added to db. " + params);
        }
    }
}
