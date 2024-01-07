package ma.hibernate.constants;

public enum Constants {
    ZERO(0);
    private final int value;
    Constants(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
