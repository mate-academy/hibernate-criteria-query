package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    private static final SessionFactory factory = HibernateUtil.getSessionFactory();
    private static final PhoneDao phoneDao = new PhoneDaoImpl(factory);

    public static void main(String[] args) {
        inject();
        Map<String, String[]> params = new HashMap<>();
        params.put("maker", new String[]{"Apple", "Samsung"});
        phoneDao.findAll(params).forEach(System.out::println);

    }

    private static void inject() {
        Phone iphoneX = new Phone();
        iphoneX.setModel("iPhone X");
        iphoneX.setColor("red");
        iphoneX.setMaker("Apple");
        iphoneX.setOs("iOS");
        iphoneX.setCountryManufactured("USA");
        phoneDao.create(iphoneX);

        Phone iphone7 = new Phone();
        iphone7.setModel("iPhone 7");
        iphone7.setColor("white");
        iphone7.setMaker("Apple");
        iphone7.setOs("iOS");
        iphone7.setCountryManufactured("USA");
        phoneDao.create(iphone7);

        Phone samsungA5 = new Phone();
        samsungA5.setModel("A5");
        samsungA5.setColor("white");
        samsungA5.setMaker("Samsung");
        samsungA5.setOs("Android");
        samsungA5.setCountryManufactured("Korea");
        phoneDao.create(samsungA5);

        Phone samsungA7White = new Phone();
        samsungA7White.setModel("A7");
        samsungA7White.setColor("white");
        samsungA7White.setMaker("Samsung");
        samsungA7White.setOs("Android");
        samsungA7White.setCountryManufactured("Korea");
        phoneDao.create(samsungA7White);

        Phone samsungA7Red = new Phone();
        samsungA7Red.setModel("A7");
        samsungA7Red.setColor("red");
        samsungA7Red.setMaker("Samsung");
        samsungA7Red.setOs("Android");
        samsungA7Red.setCountryManufactured("Korea");
        phoneDao.create(samsungA7Red);

        Phone samsungA7Black = new Phone();
        samsungA7Black.setModel("A7");
        samsungA7Black.setColor("red");
        samsungA7Black.setMaker("Samsung");
        samsungA7Black.setOs("Android");
        samsungA7Black.setCountryManufactured("Korea");
        phoneDao.create(samsungA7Black);

        Phone oppo10white = new Phone();
        oppo10white.setModel("Oppo10");
        oppo10white.setColor("white");
        oppo10white.setMaker("Oppo");
        oppo10white.setOs("Android");
        oppo10white.setCountryManufactured("China");
        phoneDao.create(oppo10white);

        Phone xiaomiRedmi5 = new Phone();
        xiaomiRedmi5.setModel("Redmi 5");
        xiaomiRedmi5.setColor("black");
        xiaomiRedmi5.setMaker("Xiaomi");
        xiaomiRedmi5.setOs("Android");
        xiaomiRedmi5.setCountryManufactured("China");
        phoneDao.create(xiaomiRedmi5);
    }
}
