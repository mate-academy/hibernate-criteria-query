package ma.hibernate;

import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;

public class Main {
    public static void main(String[] args) {
        Phone phone = new Phone();
        phone.setColor("blue");
        phone.setMaker("rostik");
        phone.setCountryManufactured("ukraine");
        phone.setOs("android");
        PhoneDao phoneDao = new PhoneDaoImpl(HibernateUtil.getSessionFactory());
        phoneDao.create(phone);
        System.out.println("ыфыщвфыащ");

    }
}
