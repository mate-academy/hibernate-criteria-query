package ma.hibernate.service.impl;

import java.util.List;
import java.util.Map;
import ma.hibernate.dao.PhoneDao;
import ma.hibernate.dao.PhoneDaoImpl;
import ma.hibernate.model.Phone;
import ma.hibernate.service.PhoneService;
import ma.hibernate.util.HibernateUtil;

public class PhoneServiceImpl implements PhoneService {
    private final PhoneDao phoneDao = new PhoneDaoImpl(HibernateUtil.getSessionFactory());

    @Override
    public Phone create(Phone phone) {
        return phoneDao.create(phone);
    }

    @Override
    public List<Phone> findAll(Map<String, String[]> params) {
        return phoneDao.findAll(params);
    }
}
