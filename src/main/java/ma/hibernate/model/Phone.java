package ma.hibernate.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Phone implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;
    private String maker;
    private String color;
    private String os;
    private String countryManufactured;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Can't make clone of " + this);
        }
    }

    @Override
    public String toString() {
        return "Phone{"
            + "id=" + id
            + ", model='" + model + '\''
            + ", maker='" + maker + '\''
            + ", color='" + color + '\''
            + ", os='" + os + '\''
            + ", countryManufactured='" + countryManufactured + '\''
            + '}';
    }
}
