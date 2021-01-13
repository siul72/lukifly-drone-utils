package co.luism.ksoft.iot.utils.configuration;

/**
 * Created by luis on 16.02.15.
 */
public enum EnumEndianess {

        //Big-endian 0x0A0B0C0D => 0x0A;0x0B;0x0C;0x0D
        //Litle-endian 0x0A0B0C0D => 0x0D;0x0C;0x0B;0x0A

    LITLE_ENDIAN,
    BIG_ENDIAN;

    public static EnumEndianess getEnum(String sTemp) {

        for(EnumEndianess e : EnumEndianess.values()){
            if(e.toString().compareToIgnoreCase(sTemp) == 0){
                return e;
            }
        }

        return null;
    }

}
