package org.apache.poi.hpsf;

public class CustomProperty extends MutableProperty {
    private String name;

    public CustomProperty() {
        this.name = null;
    }

    public CustomProperty(Property property) {
        this(property, (String) null);
    }

    public CustomProperty(Property property, String name2) {
        super(property);
        this.name = name2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public boolean equalsContents(Object o) {
        boolean equalNames;
        CustomProperty c = (CustomProperty) o;
        String name1 = c.getName();
        String name2 = getName();
        if (name1 == null) {
            equalNames = name2 == null;
        } else {
            equalNames = name1.equals(name2);
        }
        if (!equalNames || c.getID() != getID() || c.getType() != getType() || !c.getValue().equals(getValue())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (int) getID();
    }
}
