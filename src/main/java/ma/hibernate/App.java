package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class App {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        Phone iphoneX = new Phone("X", "Apple", "black", "China");
        Phone iphone7 = new Phone("7", "Apple", "white", "China");
        Phone samsungA5 = new Phone("A5", "Samsung", "", "China");
        Phone samsungA7White = new Phone("A7", "Samsung", "white", "China");
        Phone samsungA7Red = new Phone("A7", "Samsung", "red", "China");
        Phone samsungA7Black = new Phone("A7", "Samsung", "black", "China");
        Phone xiaomiRedmi5 = new Phone("5", "Xiaomi", "white", "China");

        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(iphoneX);
        phoneDao.create(iphone7);
        phoneDao.create(samsungA5);
        phoneDao.create(samsungA7White);
        phoneDao.create(samsungA7Red);
        phoneDao.create(samsungA7Black);
        phoneDao.create(xiaomiRedmi5);

        Map<String, String[]> input = new HashMap<>();
        input.put("countryManufactured", new String[]{"China"});
        input.put("maker", new String[]{"apple", "nokia", "samsung"});
        input.put("color", new String[]{"white", "red"});
        phoneDao.findAll(input).forEach(System.out::println);
    }
}
