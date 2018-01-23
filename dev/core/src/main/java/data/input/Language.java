package data.input;

public enum Language {
    EN("en"),
    DE("de"),
    ES("es");

    private final String languageCode;

    Language(final String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public String toString() {
        return languageCode;
    }
}
