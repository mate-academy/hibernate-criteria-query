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
        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white", "red"});
        insertPhones();
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        System.out.println(phoneDao.findAll(params));

    }

    private static void insertPhones() {
        Phone redmi = new Phone();
        redmi.setModel("Redmi 5");
        redmi.setColor("black");
        redmi.setMaker("Xiaomi");
        redmi.setOs("Android");
        redmi.setCountryManufactured("China");

        Phone iphone7 = new Phone();
        iphone7.setModel("iPhone 7");
        iphone7.setColor("white");
        iphone7.setMaker("Apple");
        iphone7.setOs("iOS");
        iphone7.setCountryManufactured("USA");

        Phone samsungA5 = new Phone();
        samsungA5.setModel("A5");
        samsungA5.setColor("white");
        samsungA5.setMaker("Samsung");
        samsungA5.setOs("Android");
        samsungA5.setCountryManufactured("Korea");
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(redmi);
        phoneDao.create(iphone7);
        phoneDao.create(samsungA5);
    }
}
