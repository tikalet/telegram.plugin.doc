package tikale.telegram.plugin.doc.entity;

public enum DocType {
    DOC("/doc"),
    OTHER("");

    private String text;

    private DocType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static DocType find(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return OTHER;
        }

        for (DocType ht : DocType.values()) {
            if (ht.getText().equals(pattern)) {
                return ht;
            }
        }

        return OTHER;
    }

}
