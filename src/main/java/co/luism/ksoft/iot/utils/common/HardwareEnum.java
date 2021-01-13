package co.luism.ksoft.iot.utils.common;

/**
 * Created by luis on 15.10.14.
 */
public enum HardwareEnum {

    NONE("NONE"),
    DEV_EKE("EKE Rechner"),
    DEV_CC_DISPLAY("CC Systems Display");

    private String value;

    HardwareEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }


    public static HardwareEnum getByName(String v){
        for(HardwareEnum h : HardwareEnum.values()){
            if(h.value.equals(v)){
                return h;
            }
        }
        return null;
    }

}
