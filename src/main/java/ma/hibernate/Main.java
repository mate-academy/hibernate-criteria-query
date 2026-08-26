package ma.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import org.hibernate.SessionFactory;

public class Main {
    private static final Map<String, String[]> PARAMS = new HashMap<>();
    private static SessionFactory factory;

    public static SessionFactory getSessionFactory() {
        return factory;
    }

    public static void main(String[] args) {
        Phone phone = new Phone();
        phone.setCountryManufactured("USA");
        phone.setModel("QRT");
        phone.setMaker("apple");
        phone.setColor("red");
        phone.setOs("OS");
        PhoneDao phoneDao = new PhoneDaoImpl(getSessionFactory());
        phoneDao.create(phone);
        PARAMS.put("model", new String[]{"A5", "Oppo10", "QRT", "WrongModel"});
        List<Phone> actual = phoneDao.findAll(PARAMS);
    }
}
