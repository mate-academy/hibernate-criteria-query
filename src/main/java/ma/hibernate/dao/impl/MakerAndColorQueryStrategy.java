package ma.hibernate.dao.impl;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ma.hibernate.dao.QueryStrategy;
import ma.hibernate.model.Phone;
import org.hibernate.Session;

public class MakerAndColorQueryStrategy implements QueryStrategy {
    @Override
    public List<Phone> findAll(Session session,
                               CriteriaBuilder cb,
                               CriteriaQuery<Phone> query,
                               Root<Phone> phoneRoot,
                               Map<String, String[]> params) {
        CriteriaBuilder.In<String> makerPredicate = cb.in(phoneRoot.get("maker"));
        CriteriaBuilder.In<String> colorPredicate = cb.in(phoneRoot.get("color"));

        for (String maker : params.get("maker")) {
            makerPredicate.value(maker);
        }

        for (String color : params.get("color")) {
            colorPredicate.value(color);
        }

        query.where(cb.and(makerPredicate, colorPredicate));
        return session.createQuery(query).getResultList();
    }
}
