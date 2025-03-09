package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main {
    // just to let you know that everything is working
    public static void main(String[] args) {
        Phone phone1 = new Phone();
        phone1.setColor("Red");
        phone1.setMaker("Canada");
        phone1.setModel("Samsung");
        phone1.setOs("Android");
        phone1.setCountryManufactured("China");

        Phone phone2 = new Phone();
        phone2.setColor("Blue");
        phone2.setMaker("China");
        phone2.setModel("Iphone");
        phone2.setOs("IOS");
        phone2.setCountryManufactured("China");

        Phone phone3 = new Phone();
        phone3.setColor("White");
        phone3.setMaker("Canada");
        phone3.setModel("Samsung");
        phone3.setOs("Android");
        phone3.setCountryManufactured("China");

        SessionFactory sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Phone.class)
                .buildSessionFactory();

        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(phone1);
        phoneDao.create(phone2);
        phoneDao.create(phone3);

        Map<String, String[]> params = new HashMap<>();
        params.put("color", new String[] {"Red", "White"});
        params.put("maker", new String[] {"Canada"});

        System.out.println(phoneDao.findAll(params));
    }
}
