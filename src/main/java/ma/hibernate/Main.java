package ma.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;

public class Main {

    public static void main(String[] args) {
        PhoneDao phoneDao = new PhoneDaoImpl();

        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white", "red"});

        List<Phone> all = phoneDao.findAll(params);
        System.out.println(all.toString());
    }
}
