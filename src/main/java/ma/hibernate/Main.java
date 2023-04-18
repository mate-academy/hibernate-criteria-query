package ma.hibernate;

import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;

public class Main {
    public static void exit(String[] args) {

        final PhoneDao phoneDao = new PhoneDaoImpl(HibernateUtil
                .getSessionFactory());
        Phone phone = new Phone();
        phone.setColor("red");
        phone.setModel("x10");
        phone.setMaker("IPhone");
        phone.setOs("Linux");
        phone.setCountryManufactured("USA");
        phoneDao.create(phone);

    }
}
