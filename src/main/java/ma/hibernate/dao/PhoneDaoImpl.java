package ma.hibernate.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
            return phone;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Could not create phone " + phone, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Phone> getAllPhonesQuery = criteriaBuilder.createQuery(Phone.class);
            Root<Phone> root = getAllPhonesQuery.from(Phone.class);
            if (params.size() != 0) {
                List<Predicate> parameters = new ArrayList<>();
                if (params.get("model") != null) {
                    Predicate[] modelParameters = new Predicate[params.get("model").length];
                    for (int i = 0; i < modelParameters.length; i++) {
                        modelParameters[i] = criteriaBuilder
                                .equal(root.get("model"), params.get("model")[i]);
                    }
                    parameters.add(criteriaBuilder.or(modelParameters));
                }
                if (params.get("maker") != null) {
                    Predicate[] makerParameters = new Predicate[params.get("maker").length];
                    for (int i = 0; i < makerParameters.length; i++) {
                        makerParameters[i] = criteriaBuilder
                                .equal(root.get("maker"), params.get("maker")[i]);
                    }
                    parameters.add(criteriaBuilder.or(makerParameters));
                }
                if (params.get("color") != null) {
                    Predicate[] colorParameters = new Predicate[params.get("color").length];
                    for (int i = 0; i < colorParameters.length; i++) {
                        colorParameters[i] = criteriaBuilder
                                .equal(root.get("color"), params.get("color")[i]);
                    }
                    parameters.add(criteriaBuilder.or(colorParameters));
                }
                if (params.get("os") != null) {
                    Predicate[] osParameters = new Predicate[params.get("os").length];
                    for (int i = 0; i < osParameters.length; i++) {
                        osParameters[i] = criteriaBuilder
                                .equal(root.get("os"), params.get("os")[i]);
                    }
                    parameters.add(criteriaBuilder.or(osParameters));
                }
                if (params.get("countryManufactured") != null) {
                    Predicate[] countryManufacturedParameters
                            = new Predicate[params.get("countryManufactured").length];
                    for (int i = 0; i < countryManufacturedParameters.length; i++) {
                        countryManufacturedParameters[i] = criteriaBuilder
                                .equal(root.get("countryManufactured"),
                                        params.get("countryManufactured")[i]);
                    }
                    parameters.add(criteriaBuilder.or(countryManufacturedParameters));
                }
                Predicate selectPredicate = criteriaBuilder
                        .and(parameters.toArray(Predicate[]::new));
                getAllPhonesQuery.select(root).where(selectPredicate);
            } else {
                getAllPhonesQuery.select(root);
            }
            return session.createQuery(getAllPhonesQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Could not get a list of phones from DB. ", e);
        }
    }
}
