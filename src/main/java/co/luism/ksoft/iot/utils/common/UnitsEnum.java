package co.luism.ksoft.iot.utils.common;

/**
 * Created by luis on 15.10.14.
 */
public enum  UnitsEnum {
    UNIT_NONE("NONE"),
    UNIT_VOLT("V"),
    UNIT_KILOVOLT("kV"),
    UNIT_AMPERE("A"),
    UNIT_KILOAMPERE("kA"),
    UNIT_WATT("W") ,
    UNIT_KILOWATT("kW"),
    UNIT_NEWTON("N"),
    UNIT_KILONEWTON("kN"),
    UNIT_BAR("bar"),
    UNIT_DEGREE_CELCIUS("°C"),
    UNIT_KM_PER_HOUR("km/h"),
    UNIT_METER_PER_SECOND("m/s"),
    UNIT_HOUR("h"),
    UNIT_MINUTE("min"),
    UNIT_SECOND("sek");


    private String value;

    UnitsEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String toString(){
        return value;
    }

    public static UnitsEnum getEnum(String sDatentyp) {
        for(UnitsEnum t : UnitsEnum.values()){
            if(t.getValue().equals(sDatentyp)){
                return t;
            }
        }

        return null;
    }

}
