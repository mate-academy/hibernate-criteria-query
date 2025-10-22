package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;

public interface PhoneDao {
    Phone create(Phone phone);

    // Use CriteriaQuery for this method
    /*Map<String, String[]> params = new HashMap<>();
params.put("countryManufactured", new String[]{"China"};
params.put("maker", new String[]{"apple", "nokia", "samsung"};
params.put("color", new String[]{"white", "red"};*/
    List<Phone> findAll(Map<String, String[]> params);
}
