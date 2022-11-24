package ma.hibernate;

import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static PhoneDao phoneDao = new PhoneDaoImpl(new Configuration()
            .configure()
            .buildSessionFactory());

    public static void main(String[] args) {
        Phone iPhone = new Phone();
        iPhone.setColor("red");
        iPhone.setMaker("iPhone");
        iPhone.setModel("X");
        iPhone.setOs("iOS");
        iPhone.setCountryManufactured("USA");

        phoneDao.create(iPhone);
        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"USA"});
        params.put("model", new String[]{"X"});
        List<Phone> all = phoneDao.findAll(params);
        System.out.println(all);
    }
}
