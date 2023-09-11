package ma.hibernate.dao;

import ma.hibernate.model.Phone;

public class Phones {
    public static final Phone IPHONE_X = new Phone();
    public static final Phone IPHONE_7 = new Phone();
    public static final Phone SAMSUNG_A5 = new Phone();
    public static final Phone SAMSUNG_A7_WHITE = new Phone();
    public static final Phone SAMSUNG_A7_RED = new Phone();
    public static final Phone SAMSUNG_A7_BLACK = new Phone();
    public static final Phone OPPO_10_WHITE = new Phone();
    public static final Phone XIAOMI_REDMI_5 = new Phone();

    static {
        // iPhoneX
        IPHONE_X.setModel("iPhone X");
        IPHONE_X.setColor("red");
        IPHONE_X.setMaker("Apple");
        IPHONE_X.setOs("iOS");
        IPHONE_X.setCountryManufactured("USA");

        // iPhone7
        IPHONE_7.setModel("iPhone 7");
        IPHONE_7.setColor("white");
        IPHONE_7.setMaker("Apple");
        IPHONE_7.setOs("iOS");
        IPHONE_7.setCountryManufactured("USA");

        // samsungA5
        SAMSUNG_A5.setModel("A5");
        SAMSUNG_A5.setColor("white");
        SAMSUNG_A5.setMaker("Samsung");
        SAMSUNG_A5.setOs("Android");
        SAMSUNG_A5.setCountryManufactured("Korea");

        // samsung A7 white
        SAMSUNG_A7_WHITE.setModel("A7");
        SAMSUNG_A7_WHITE.setColor("white");
        SAMSUNG_A7_WHITE.setMaker("Samsung");
        SAMSUNG_A7_WHITE.setOs("Android");
        SAMSUNG_A7_WHITE.setCountryManufactured("Korea");

        // samsung A7 red
        SAMSUNG_A7_RED.setModel("A7");
        SAMSUNG_A7_RED.setColor("red");
        SAMSUNG_A7_RED.setMaker("Samsung");
        SAMSUNG_A7_RED.setOs("Android");
        SAMSUNG_A7_RED.setCountryManufactured("Korea");

        // samsung A7 black
        SAMSUNG_A7_BLACK.setModel("A7");
        SAMSUNG_A7_BLACK.setColor("red");
        SAMSUNG_A7_BLACK.setMaker("Samsung");
        SAMSUNG_A7_BLACK.setOs("Android");
        SAMSUNG_A7_BLACK.setCountryManufactured("Korea");

        // Oppo 10 white
        OPPO_10_WHITE.setModel("Oppo10");
        OPPO_10_WHITE.setColor("white");
        OPPO_10_WHITE.setMaker("Oppo");
        OPPO_10_WHITE.setOs("Android");
        OPPO_10_WHITE.setCountryManufactured("China");

        // Xiaomi Redmi 5
        XIAOMI_REDMI_5.setModel("Redmi 5");
        XIAOMI_REDMI_5.setColor("black");
        XIAOMI_REDMI_5.setMaker("Xiaomi");
        XIAOMI_REDMI_5.setOs("Android");
        XIAOMI_REDMI_5.setCountryManufactured("China");
    }
}
