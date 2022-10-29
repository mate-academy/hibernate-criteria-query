package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start");
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white"});

        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);

        phoneDao.create(new Phone("Model 1", "apple", "red", "Android", "China"));
        phoneDao.create(new Phone("Model 2", "nokia", "red", "Android", "Japan"));
        phoneDao.create(new Phone("Model 3", "samsung", "red", "Android", "China"));
        phoneDao.create(new Phone("Model 4", "apple", "white", "Android", "China"));
        phoneDao.create(new Phone("Model 5", "nokia", "white", "Android", "USA"));
        phoneDao.create(new Phone("Model 6", "samsung", "green", "Android", "China"));
        System.out.println(phoneDao.findAll(params));
    }
}
