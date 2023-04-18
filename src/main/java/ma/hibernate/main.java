package ma.hibernate;

import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.util.HibernateUtil;

import java.util.HashMap;
import java.util.Map;


public class main {
    public static void main(String[] args) {

        Map<String, String[]> params = new HashMap<>();
        params.put("countryManufactured", new String[]{"China"});
        params.put("maker", new String[]{"apple", "nokia", "samsung"});
        params.put("color", new String[]{"white", "red"});


        for (Map.Entry<String, String[]> op : params.entrySet() ) {
           for (String s : op.getValue()){

      //         System.out.println(op.getKey() + " " + s);

           }
       }

        PhoneDaoImpl phoneDao = new PhoneDaoImpl(HibernateUtil.getSessionFactory());
        Phone phone = new Phone();
        phone.setColor("red");
        phone.setModel("x10");
        phone.setMaker("Iphone");
        phone.setOs("linux");
        phone.setCountryManufactured("Usa");

        phoneDao.create(phone);

    }
}
