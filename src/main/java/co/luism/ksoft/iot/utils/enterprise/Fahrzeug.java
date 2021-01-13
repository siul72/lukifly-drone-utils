package co.luism.ksoft.iot.utils.enterprise;

import co.luism.ksoft.iot.utils.common.CognitioUtils;

/**
 * Created by luis on 15.10.14.
 */
public class Fahrzeug {
    private String fahrzeugName;
    private String einbauort;
    private final String mD5Hash;

    public Fahrzeug(String name, String index){
        this.fahrzeugName = name;
        this.einbauort = index;
        this.mD5Hash = CognitioUtils.getMD5Hash(this.fahrzeugName + this.einbauort);
    }

    public Fahrzeug(String name, String index, String md5){
        this.fahrzeugName = name;
        this.einbauort = index;
        this.mD5Hash = md5;
    }

    public String getFahrzeugName() {
        return this.fahrzeugName;
    }

    public void setFahrzeugName(String sFahrzeugName) {
        this.fahrzeugName = sFahrzeugName;
    }

    public String getEinbauort() {
        return einbauort;
    }

    public void setEinbauort(String sEinbauort) {
        this.einbauort = sEinbauort;
    }

    public String getMD5Hash() {
        return mD5Hash;
    }

    @Override
    public String toString(){
        return this.fahrzeugName;
    }


}
