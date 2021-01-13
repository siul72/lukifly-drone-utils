package co.luism.ksoft.iot.utils.common;

/**
 * Created by luis on 16.10.14.
 */
public enum  UICCategoryEnum {
    /** UIC Kategorie A, A1, B, B1, C, M */
    NONE("NONE"),A("A"),A1("A1"),B("B"),B1("B1"),C("C"),M("M");

    private final String value;

    UICCategoryEnum(String v){
        this.value = v;
    }

    public String getValue(){
        return this.value;
    }
}
