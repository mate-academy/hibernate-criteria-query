package ma.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory = HibernateUtil.init();

    private HibernateUtil() {
    }

    private static SessionFactory init() {
        return new Configuration().configure().buildSessionFactory();
    }

    public static SessionFactory getFactory() {
        return sessionFactory;
    }
}
