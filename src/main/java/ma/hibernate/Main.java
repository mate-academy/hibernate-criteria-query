package ma.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        Phone iphoneX = new Phone();
        iphoneX.setModel("X");
        iphoneX.setMaker("Apple");
        iphoneX.setColor("black");
        iphoneX.setCountryManufactured("USA");
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(iphoneX);

        Phone iphone7 = new Phone();
        iphone7.setModel("7");
        iphone7.setMaker("Apple");
        iphone7.setColor("red");
        iphone7.setCountryManufactured("USA");
        phoneDao.create(iphone7);

        Phone samsungA5 = new Phone();
        samsungA5.setModel("A5");
        samsungA5.setMaker("Samsung");
        samsungA5.setColor("black");
        samsungA5.setCountryManufactured("South Korea");
        phoneDao.create(samsungA5);

        Phone samsungA7 = new Phone();
        samsungA7.setModel("A7");
        samsungA7.setMaker("Samsung");
        samsungA7.setColor("white");
        samsungA7.setCountryManufactured("South Korea");
        phoneDao.create(samsungA7);
        
        Map<String, String[]> params = new HashMap<>();
        params.put("maker", new String[]{"Apple", "Samsung"});
        params.put("color", new String[]{"black", "white"});

        List<Phone> foundPhones = phoneDao.findAll(params);
        System.out.println("Found phones:");
        for (Phone phone : foundPhones) {
            System.out.println(phone);
        }

        sessionFactory.close();
    }
}
