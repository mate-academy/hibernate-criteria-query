package ma.hibernate;

import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);
        Map<String, String[]> params = Map.of(
                "color", new String[]{"", "white"},
                "maker", new String[]{"Samsung", "Nokia", "Apple"}
        );
        System.out.println(phoneDao.findAll(params));
    }
}
