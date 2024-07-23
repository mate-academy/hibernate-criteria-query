package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white", "red"});
        Phone phone = new Phone();
        phone.setCountryManufactured("China");
        phone.setMaker("apple");
        phone.setModel("X");
        phone.setColor("white");
        phone.setOs("Ios 15");
        PhoneDao phoneDao = new PhoneDaoImpl(HibernateUtil.getSessionFactory());
        phoneDao.create(phone);
        phoneDao.findAll(params).forEach(System.out::println);
    }
}
