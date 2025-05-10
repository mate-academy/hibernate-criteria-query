package ma.hibernate.dao;

import java.util.List;
import java.util.Map;
import ma.hibernate.model.Phone;
import org.hibernate.SessionFactory;

public class PhoneDaoImpl extends AbstractDao implements PhoneDao {
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Phone create(Phone phone) {
        super.create(phone);
        return phone;
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        return super.findAll(params, Phone.class);
    }
}
