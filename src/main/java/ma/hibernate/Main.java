package ma.hibernate;

import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Map<String,String[]> params = new HashMap<>();
        try (SessionFactory sessionFactory = HibernateUtil.getSessionFactory()){
            PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
            insertPhones(phoneDao);
            params.put("maker", new String[]{"Apple"});
            phoneDao.findAll(params);
        }

    }

    public static void insertPhones(PhoneDao phoneDao) {
        Phone iphoneX = (Phone) Phones.iphoneX.clone();
        verifyCreatePhoneWorks(phoneDao, iphoneX, 1L);

        Phone iphone7 = (Phone) Phones.iphone7.clone();
        verifyCreatePhoneWorks(phoneDao, iphone7, 2L);

        Phone samsungA5 = (Phone) Phones.samsungA5.clone();
        verifyCreatePhoneWorks(phoneDao, samsungA5, 3L);

        Phone samsungA7White = (Phone) Phones.samsungA7White.clone();
        verifyCreatePhoneWorks(phoneDao, samsungA7White, 4L);

        Phone samsungA7Red = (Phone) Phones.samsungA7Red.clone();
        verifyCreatePhoneWorks(phoneDao, samsungA7Red, 5L);

        Phone samsungA7Black = (Phone) Phones.samsungA7Black.clone();
        verifyCreatePhoneWorks(phoneDao, samsungA7Black, 6L);

        Phone oppo10white = (Phone) Phones.oppo10white.clone();
        verifyCreatePhoneWorks(phoneDao, oppo10white, 7L);

        Phone xiaomiRedmi5 = (Phone) Phones.xiaomiRedmi5.clone();
        verifyCreatePhoneWorks(phoneDao, xiaomiRedmi5, 8L);
    }

    private static void verifyCreatePhoneWorks(PhoneDao phoneDao, Phone phone, Long expectedId) {
        phoneDao.create(phone);
    }
}
