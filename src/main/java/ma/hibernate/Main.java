package ma.hibernate;

import java.util.HashMap;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("color", new String[]{"", "white"});
        params.put("maker", new String[]{"Samsung", "nokia", "Apple"});
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        System.out.println(phoneDao.findAll(params));
    }
}
