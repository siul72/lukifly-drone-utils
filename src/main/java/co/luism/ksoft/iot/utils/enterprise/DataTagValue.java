package co.luism.ksoft.iot.utils.enterprise;

import co.luism.ksoft.iot.utils.common.CognitioTypeEnum;
import co.luism.ksoft.iot.utils.common.UnitsEnum;

/**
 * Created by luis on 15.10.14.
 */
public class DataTagValue implements Comparable {

    // Attribute für jedes Element
    private Integer id;
    private String name;
    private String commentar;
    private String umrechnung; // z.B. +1;/45.2;-3
    private boolean signed;
    private boolean ereignis;  // Löst diese Signal einen Eintrag aus?
    private DataTagEvent event;
    private UnitsEnum unit;
    private CognitioTypeEnum type;

    public DataTagValue(){

    }

    public DataTagValue(Integer id, String sUmfeldname, String sKommentar, UnitsEnum sUnit, CognitioTypeEnum sTyp, String sUmwandlung, boolean xSigned, boolean xAlarm)
    {
        this.id = id;
        this.name = sUmfeldname;
        this.commentar = sKommentar;
        this.unit = sUnit;
        this.type =   sTyp;
        this.umrechnung = sUmwandlung;
        this.signed = xSigned;
        this.ereignis = xAlarm;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        if(event != null){
            event.setName(name);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommentar() {
        return commentar;
    }

    public void setCommentar(String commentar) {
        this.commentar = commentar;
    }

    public String getUmrechnung() {
        return umrechnung;
    }

    public void setUmrechnung(String umrechnung) {
        this.umrechnung = umrechnung;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public boolean isEreignis() {
        return ereignis;
    }

    public void setEreignis(boolean ereignis) {
        this.ereignis = ereignis;
    }

    public DataTagEvent getEvent() {
        return event;
    }

    public void setEvent(DataTagEvent event) {
        this.event = event;
    }

    public UnitsEnum getUnit() {
        return unit;
    }

    public void setUnit(UnitsEnum unit) {
        this.unit = unit;
    }

    public CognitioTypeEnum getType() {
        return type;
    }

    public void setType(CognitioTypeEnum type) {
        this.type = type;
    }

    @Override
    public String toString(){
            return this.name;
        }

    @Override
    public int compareTo(Object o){

        if(o instanceof DataTagValue){
            return this.id.compareTo(((DataTagValue)o).getId());
        }

        return 0;
    }

}

