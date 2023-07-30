package ma.hibernate.service;

import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;

public interface PhoneService {
    Phone create(Phone phone);

    List<Phone> findAll(Map<String, String[]> params);
}
