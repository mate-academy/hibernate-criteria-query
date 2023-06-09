package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    public static final Phone iphoneX = new Phone();
    public static final Phone iphone7 = new Phone();
    public static final Phone samsungA5 = new Phone();
    public static final Phone samsungA7White = new Phone();
    public static final Phone samsungA7Red = new Phone();
    public static final Phone samsungA7Black = new Phone();
    public static final Phone oppo10white = new Phone();
    public static final Phone xiaomiRedmi5 = new Phone();

    static {
        // iPhoneX
        iphoneX.setModel("iPhone X");
        iphoneX.setColor("red");
        iphoneX.setMaker("Apple");
        iphoneX.setOs("iOS");
        iphoneX.setCountryManufactured("USA");

        // iPhone7
        iphone7.setModel("iPhone 7");
        iphone7.setColor("white");
        iphone7.setMaker("Apple");
        iphone7.setOs("iOS");
        iphone7.setCountryManufactured("USA");

        // samsungA5
        samsungA5.setModel("A5");
        samsungA5.setColor("white");
        samsungA5.setMaker("Samsung");
        samsungA5.setOs("Android");
        samsungA5.setCountryManufactured("Korea");

        // samsung A7 white
        samsungA7White.setModel("A7");
        samsungA7White.setColor("white");
        samsungA7White.setMaker("Samsung");
        samsungA7White.setOs("Android");
        samsungA7White.setCountryManufactured("Korea");

        // samsung A7 red
        samsungA7Red.setModel("A7");
        samsungA7Red.setColor("red");
        samsungA7Red.setMaker("Samsung");
        samsungA7Red.setOs("Android");
        samsungA7Red.setCountryManufactured("Korea");

        // samsung A7 black
        samsungA7Black.setModel("A7");
        samsungA7Black.setColor("red");
        samsungA7Black.setMaker("Samsung");
        samsungA7Black.setOs("Android");
        samsungA7Black.setCountryManufactured("Korea");

        // Oppo 10 white
        oppo10white.setModel("Oppo10");
        oppo10white.setColor("white");
        oppo10white.setMaker("Oppo");
        oppo10white.setOs("Android");
        oppo10white.setCountryManufactured("China");

        // Xiaomi Redmi 5
        xiaomiRedmi5.setModel("Redmi 5");
        xiaomiRedmi5.setColor("black");
        xiaomiRedmi5.setMaker("Xiaomi");
        xiaomiRedmi5.setOs("Android");
        xiaomiRedmi5.setCountryManufactured("China");
    }

    public static void main(String[] args) {
        Map<String, String[]> params = new HashMap<>();
        params.put("color", new String[]{"black"});
        params.put("maker", new String[]{"Apple", "Xiaomi", "Oppo"});
        params.put("countryManufactured", new String[]{"USA", "China"});

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);

        phoneDao.create(iphoneX);
        phoneDao.create(samsungA5);
        phoneDao.create(xiaomiRedmi5);
        System.out.println(phoneDao.findAll(params));
    }
}
