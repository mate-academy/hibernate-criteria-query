package ma.hibernate.dao.impl;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ma.hibernate.dao.QueryStrategy;
import ma.hibernate.model.Phone;
import org.hibernate.Session;

public class MakerAndColorAndCountryQueryStrategy implements QueryStrategy {
    @Override
    public List<Phone> findAll(Session session,
                               CriteriaBuilder cb,
                               CriteriaQuery<Phone> query,
                               Root<Phone> phoneRoot,
                               Map<String, String[]> params) {
        CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
        CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));
        CriteriaBuilder.In<String> countryPredicate = cb.in(phoneRoot.get("countryManufactured"));

        for (String maker : params.get("maker")) {
            makerPredicate.value(maker);
        }

        for (String color : params.get("color")) {
            colorPredicate.value(color);
        }

        for (String country : params.get("countryManufactured")) {
            countryPredicate.value(country);
        }

        query.where(cb.and(makerPredicate, colorPredicate, countryPredicate));
        return session.createQuery(query).getResultList();
    }
}
