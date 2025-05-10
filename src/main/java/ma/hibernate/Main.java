package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public static void main(String[] args) {
        injectPhones();
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        Map<String, String[]> map = new HashMap<>();
        map.put("os", new String[] {"Android"});
        System.out.println(phoneDao.findAll(map));
    }

    private static void injectPhones() {
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        phoneDao.create(new Phone("iPhone X", "Apple", "black", "IOS","USA"));
        phoneDao.create(new Phone("S19", "Samsung", "red", "Android", "China"));
        phoneDao.create(new Phone("op10", "Oppo", "white", "Android", "Korea"));
    }
}
