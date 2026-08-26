package ma.hibernate;

import java.util.HashMap;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class Main {
    private static final SessionFactory sessionFactory =
            HibernateUtil.getSessionFactory();
    private static final PhoneDao phoneDao = new PhoneDaoImpl(sessionFactory);

    public static void main(String[] args) {

        injectPhones();

        HashMap<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white", "red"});

        phoneDao.findAll(params).forEach(System.out::println);
    }

    private static void injectPhones() {

        phoneDao.create(new Phone("Italy", "samsung", "red"));
        phoneDao.create(new Phone("China", "nokia", "red"));
        phoneDao.create(new Phone("Canada", "apple", "red"));
        phoneDao.create(new Phone("China", "xiaomi", "white"));
        phoneDao.create(new Phone("France", "nokia", "black"));
        phoneDao.create(new Phone("China", "samsung", "white"));
    }
}
