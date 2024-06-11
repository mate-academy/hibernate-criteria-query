package ma.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        Map<String, String[]> params = new HashMap<>();

        PhoneDao phoneDao = new PhoneDaoImpl(HibernateUtil.getSessionFactory());
        insertPhones(phoneDao);

        List<Phone> phones = phoneDao.findAll(params);
        System.out.println(phones);
    }

    private static void insertPhones(PhoneDao phoneDao) {
        Phone iphoneX = (Phone) Phones.iphoneX.clone();
        phoneDao.create(iphoneX);

        Phone iphone7 = (Phone) Phones.iphone7.clone();
        phoneDao.create(iphone7);

        Phone samsungA5 = (Phone) Phones.samsungA5.clone();
        phoneDao.create(samsungA5);

        Phone samsungA7White = (Phone) Phones.samsungA7White.clone();
        phoneDao.create(samsungA7White);

        Phone samsungA7Red = (Phone) Phones.samsungA7Red.clone();
        phoneDao.create(samsungA7Red);

        Phone samsungA7Black = (Phone) Phones.samsungA7Black.clone();
        phoneDao.create(samsungA7Black);

        Phone oppo10white = (Phone) Phones.oppo10white.clone();
        phoneDao.create(oppo10white);

        Phone xiaomiRedmi5 = (Phone) Phones.xiaomiRedmi5.clone();
        phoneDao.create(xiaomiRedmi5);
    }
}
