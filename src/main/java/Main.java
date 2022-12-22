import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        PhoneDao phoneDao = new PhoneDaoImpl(HibernateUtil.getSessionFactory());
        Phone phone = new Phone("iPhone x", "Apple", "white", "apple", "China");
        Phone phone2 = new Phone("iPhone x", "Apple", "blue", "apple", "China");
        Phone phone3 = new Phone("iPhone 10", "Apple", "red", "apple", "China");
        phoneDao.create(phone);
        phoneDao.create(phone2);
        phoneDao.create(phone3);
        Map<String, String[] > map = new HashMap<>();
        map.put("color", new String[]{"red", "blue"});
        map.put("model", new String[]{"iPhone x"});
        System.out.println(phoneDao.findAll(map));
    }
}
