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
        Phone xi = new Phone();
        xi.setModel("Xi");
        xi.setColor("black");
        xi.setMaker("Xiaomi");
        xi.setOs("Android");
        xi.setCountryManufactured("China");

        Phone iphone14 = new Phone();
        iphone14.setModel("iPhone 14");
        iphone14.setColor("white");
        iphone14.setMaker("Apple");
        iphone14.setOs("IOS");
        iphone14.setCountryManufactured("USA");

        Phone samsungS23 = new Phone();
        samsungS23.setModel("samsungS23");
        samsungS23.setColor("white");
        samsungS23.setMaker("Samsung");
        samsungS23.setOs("Android");
        samsungS23.setCountryManufactured("Korea");
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(xi);
        phoneDao.create(iphone14);
        phoneDao.create(samsungS23);
    }
}
