package ma.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Phone nokia = new Phone();
        nokia.setColor("white");
        nokia.setModel("nokia");
        nokia.setOs("os");
        nokia.setMaker("nokia");
        nokia.setCountryManufactured("China");
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(nokia);
        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white", "red"});
        List<Phone> phones = phoneDao.findAll(params);
        for (Phone phone : phones) {
            System.out.println(phone);
        }
    }
}
