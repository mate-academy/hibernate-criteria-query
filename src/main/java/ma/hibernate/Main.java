package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        final PhoneDao phoneDao =
                new PhoneDaoImpl(HibernateUtil.getSessionFactory());
        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white", "red"});

        phoneDao.findAll(params).forEach(x -> System.out.println(x));

    }
}
