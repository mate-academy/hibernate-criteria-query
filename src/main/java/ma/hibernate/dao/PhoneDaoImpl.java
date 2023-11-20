package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.HibernateException;
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
            transaction = session.getTransaction();
            session.persist(phone);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.commit();
            }
            throw new RuntimeException("Failed to add phone to db" + phone, e);
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
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> criteriaQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = criteriaQuery.from(Phone.class);

            Predicate[] predicates = buildPredicates(params, criteriaBuilder, root);
            criteriaQuery.where(predicates);

            return session.createQuery(criteriaQuery).getResultList();
        } catch (HibernateException e) {
            throw new RuntimeException("Failed to retrieve phones from db", e);
        }
    }

    private Predicate[] buildPredicates(Map<String, String[]> params, CriteriaBuilder criteriaBuilder, Root<Phone> root) {
        List<Predicate> predicateList = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String parameter = entry.getKey();
            String[] values = entry.getValue();

            switch (parameter) {
                case "color":
                    CriteriaBuilder.In<String> colorInClause = criteriaBuilder.in(root.get("color"));
                    for (String color : values) {
                        colorInClause.value(color);
                    }
                    predicateList.add(colorInClause);
                    break;

                case "producer":
                    CriteriaBuilder.In<String> producerInClause = criteriaBuilder.in(root.get("maker"));
                    for (String producer : values) {
                        producerInClause.value(producer);
                    }
                    predicateList.add(producerInClause);
                    break;

                case "countryManufactured":
                    CriteriaBuilder.In<String> countryManufacturedInClause = criteriaBuilder.in(root.get("countryManufactured"));
                    for (String country : values) {
                        countryManufacturedInClause.value(country);
                    }
                    predicateList.add(countryManufacturedInClause);
                    break;
            }
        }

        return predicateList.toArray(new Predicate[0]);
    }



}
