package ma.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = initSesionFactory();

    private static SessionFactory initSesionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Can't create session factory ", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
