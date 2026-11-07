package ma.yahyas.musicrec.theory.enums;

public enum Decade {
    PRE_1960S("Pre-1960s"),
    DECADE_1970S("1970s"),
    DECADE_1980S("1980s"),
    DECADE_1990S("1990s"),
    DECADE_2000S("2000s"),
    DECADE_2010S("2010s"),
    DECADE_2020S("2020s");

    private final String label;

    Decade(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}