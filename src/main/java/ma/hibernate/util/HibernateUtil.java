package ma.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory = HibernateUtil.init();

    private HibernateUtil() {
    }

    private static SessionFactory init() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Can't create SessionFactory", e);
        }
    }

    public static SessionFactory getFactory() {
        return sessionFactory;
    }
}
