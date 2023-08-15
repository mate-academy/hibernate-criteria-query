package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ma.hibernate.model.Phone;
import org.hibernate.Session;

public interface QueryStrategy {
    List<Phone> findAll(Session session,
                        CriteriaBuilder cb,
                        CriteriaQuery<Phone> query,
                        Root<Phone> phoneRoot,
                        Map<String, String[]> params);
}
