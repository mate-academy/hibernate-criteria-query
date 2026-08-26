package ma.hibernate.dao;

import ma.hibernate.model.Phone;

public class Phones {
    public static final Phone IPHONEX = new Phone();
    public static final Phone IPHONE7 = new Phone();
    public static final Phone SAMSUNGA5 = new Phone();
    public static final Phone SAMSUNGA7WHITE = new Phone();
    public static final Phone SAMSUNGA7RED = new Phone();
    public static final Phone SAMSUNGA7BLACK = new Phone();
    public static final Phone OPPO10WHITE = new Phone();
    public static final Phone XIOMIREDMI5 = new Phone();

    static {
        // iPhoneX
        IPHONEX.setModel("iPhone X");
        IPHONEX.setColor("red");
        IPHONEX.setMaker("Apple");
        IPHONEX.setOs("iOS");
        IPHONEX.setCountryManufactured("USA");

        // iPhone7
        IPHONE7.setModel("iPhone 7");
        IPHONE7.setColor("white");
        IPHONE7.setMaker("Apple");
        IPHONE7.setOs("iOS");
        IPHONE7.setCountryManufactured("USA");

        // samsungA5
        SAMSUNGA5.setModel("A5");
        SAMSUNGA5.setColor("white");
        SAMSUNGA5.setMaker("Samsung");
        SAMSUNGA5.setOs("Android");
        SAMSUNGA5.setCountryManufactured("Korea");

        // samsung A7 white
        SAMSUNGA7WHITE.setModel("A7");
        SAMSUNGA7WHITE.setColor("white");
        SAMSUNGA7WHITE.setMaker("Samsung");
        SAMSUNGA7WHITE.setOs("Android");
        SAMSUNGA7WHITE.setCountryManufactured("Korea");

        // samsung A7 red
        SAMSUNGA7RED.setModel("A7");
        SAMSUNGA7RED.setColor("red");
        SAMSUNGA7RED.setMaker("Samsung");
        SAMSUNGA7RED.setOs("Android");
        SAMSUNGA7RED.setCountryManufactured("Korea");

        // samsung A7 black
        SAMSUNGA7BLACK.setModel("A7");
        SAMSUNGA7BLACK.setColor("red");
        SAMSUNGA7BLACK.setMaker("Samsung");
        SAMSUNGA7BLACK.setOs("Android");
        SAMSUNGA7BLACK.setCountryManufactured("Korea");

        // Oppo 10 white
        OPPO10WHITE.setModel("Oppo10");
        OPPO10WHITE.setColor("white");
        OPPO10WHITE.setMaker("Oppo");
        OPPO10WHITE.setOs("Android");
        OPPO10WHITE.setCountryManufactured("China");

        // Xiaomi Redmi 5
        XIOMIREDMI5.setModel("Redmi 5");
        XIOMIREDMI5.setColor("black");
        XIOMIREDMI5.setMaker("Xiaomi");
        XIOMIREDMI5.setOs("Android");
        XIOMIREDMI5.setCountryManufactured("China");
    }
}
