package co.luism.ksoft.iot.utils.common;

/**
 * Created by luis on 15.10.14.
 */
public enum CategoryEnum {

    CAT_A("A"),
    CAT_B("B"),
    CAT_M("M");  //{"NONE", "A", "A1", "B", "B1", "C", "M"};

    private String value;

    CategoryEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
