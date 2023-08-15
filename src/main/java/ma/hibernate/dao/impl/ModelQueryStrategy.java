package ma.hibernate.dao.impl;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ma.hibernate.dao.QueryStrategy;
import ma.hibernate.model.Phone;
import org.hibernate.Session;

public class ModelQueryStrategy implements QueryStrategy {
    @Override
    public List<Phone> findAll(Session session,
                               CriteriaBuilder cb,
                               CriteriaQuery<Phone> query,
                               Root<Phone> phoneRoot,
                               Map<String, String[]> params) {
        CriteriaBuilder.In<String> modelPredicate = cb.in(phoneRoot.get("model"));

        for (String model : params.get("model")) {
            modelPredicate.value(model);
        }

        query.where(modelPredicate);
        return session.createQuery(query).getResultList();
    }
}
