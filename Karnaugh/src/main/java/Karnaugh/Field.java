package Karnaugh;

// A class that stores data for a Karnaugh table tile
class Field {
    int value;
    ReplacementSource replacementSource;

    public Field() {
        clear();
    }

    public Field(int value, ReplacementSource replacementSource) {
        set(value, replacementSource);
    }

    public Field(Field field) {
        set(field);
    }

    public void clear() {
        set(-1, ReplacementSource.Improper);
    }

    public void set(int value, ReplacementSource replacementSource) {
        this.value = value;
        this.replacementSource = replacementSource;
    }

    public void set(Field field) {
        this.value = field.value;
        this.replacementSource = field.replacementSource;
    }

    public int getValue() {
        return value;
    }

    public ReplacementSource getReplacementSource() {
        return replacementSource;
    }

    @Override
    public boolean equals(Object objField)
    {
        if(super.equals(objField))
        {
            return true;
        }
        if(!(objField instanceof Field))
        {
            return false;
        }
        Field Field = (Field) objField;
        return (value == Field.value && replacementSource.equals(Field.replacementSource));
    }
}