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
        Phone phone = new Phone();
        phone.setModel("iPhone X");
        phone.setMaker("Apple");
        phone.setColor("red");
        phone.setOs("Mac Os");
        phone.setCountryManufactured("China");

        Phone phone2 = new Phone();
        phone2.setModel("iPhone 8");
        phone2.setMaker("Apple");
        phone2.setColor("black");
        phone2.setOs("Mac Os");
        phone2.setCountryManufactured("China");

        Phone phone3 = new Phone();
        phone3.setModel("Samsung S10");
        phone3.setMaker("Samsung");
        phone3.setColor("red");
        phone3.setOs("Android");
        phone3.setCountryManufactured("Korea");

        Phone phone4 = new Phone();
        phone3.setModel("Samsung S10");
        phone3.setMaker("Samsung");
        phone3.setColor("black");
        phone3.setOs("Android");
        phone3.setCountryManufactured("Korea");

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(phone);
        phoneDao.create(phone2);
        phoneDao.create(phone3);
        phoneDao.create(phone3);

        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[] {"Korea"});
        params.put("maker", new String[] {"Samsung"});
        params.put("color", new String[] {"red"});

        System.out.println(phoneDao.findAll(params));
    }
}
