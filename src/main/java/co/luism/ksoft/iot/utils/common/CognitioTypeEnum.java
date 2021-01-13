package co.luism.ksoft.iot.utils.common;

/**
 * Created by luis on 15.10.14.
 */
public enum  CognitioTypeEnum {

    TYP_NONE("NONE"),
    TYP_BIT("BIT"),
    TYP_BYTE("BYTE"),
    TYP_WORD("WORD"), // 2 Byte
    TYP_DWORD("DWORD"), // 4 Byte
    TYP_INT("INT"), //2 Byte
    TYP_DINT ("DINT"), // 4 Byte
    TYP_REAL("REAL"); // 4 Byte

    private final String value;

    private CognitioTypeEnum(String value) {
        this.value = value;


    }

    public String getValue() {
        return value;
    }

    public Integer getSize(){

        switch (this){
            case TYP_BIT:
                return 1;
            case TYP_BYTE:
                return 8;
            case TYP_WORD:
            case TYP_INT:
                return 16;
            case TYP_DWORD:
            case TYP_DINT:
            case TYP_REAL:
                return 32;
            default:
                return 0;
        }

    }

    @Override
    public String toString(){
        return value;
    }

    public static CognitioTypeEnum getEnum(String sDatentyp) {
        for(CognitioTypeEnum t : CognitioTypeEnum.values()){
            if(t.getValue().equals(sDatentyp)){
                return t;
            }
        }

        return null;
    }
}
