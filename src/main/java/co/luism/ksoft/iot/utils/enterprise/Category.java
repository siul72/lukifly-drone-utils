package co.luism.ksoft.iot.utils.enterprise;

import java.util.*;

/**
 * Created by luis on 15.10.14.
 */
public class Category {

    private final Integer id;
    private final String name;
    private DataBuffer buffer;
    private Set<DataTagValue> myDigitalSignals;
    private Set<DataTagValue> myAnalogSignals;

     
    public Category(Integer id, String sName)
    {
        this.id = id;
        myDigitalSignals = new TreeSet<>();
        myAnalogSignals = new TreeSet<>();
        this.name = sName;
        this.buffer = null;
    }

    public Integer getId() {
        return id;
    }

    /**
     * Grösse der Kategorie in Bytes. 
     * Aufbau: Zuerst analoge Werte dann digitale. 8 digitale Werte ergeben ein Byte
     *
     * @return Grösse in Bytes
     */
    public int getSizeInBytes()
    {
        int iSize = 0;
        
        for(DataTagValue t : myAnalogSignals){
            switch(t.getType()){
                 
                case TYP_BYTE:
                    iSize += 1;
                break;
                case TYP_WORD:
                    iSize += 2;
                break;
                case TYP_DINT:
                case TYP_DWORD:
                case TYP_REAL:
                    iSize += 4;
                break;
            }
        }
 

        int iDigtal = myDigitalSignals.size();
        iSize += (iDigtal / 8);
        if(iDigtal % 8 > 0){
            iSize++;
        }

        return iSize;
    }

    public String getName() {
        return name;
    }

    public DataBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(DataBuffer buffer) {
        this.buffer = buffer;
    }

    public Set<DataTagValue> getMyDigitalSignals() {
        return myDigitalSignals;
    }

    public void setMyDigitalSignals(Set<DataTagValue> myDigitalSignals) {
        this.myDigitalSignals = myDigitalSignals;
    }

    public Set<DataTagValue> getMyAnalogSignals() {
        return myAnalogSignals;
    }

    public void setMyAnalogSignals(Set<DataTagValue> myAnalogSignals) {
        this.myAnalogSignals = myAnalogSignals;
    }

    
    public boolean contains(DataTagValue t)
    {
        
        if(myAnalogSignals.contains(t)){
            return  true;
        }
        
        if(myDigitalSignals.contains(t)){
            return true;
        }
        
        return false;
    }

     
    public DataTagValue contains(String sUmfeldvariable)
    {
        for(DataTagValue t : myAnalogSignals){
            if(t.getName().equals(sUmfeldvariable)){
                return  t;
            }
        }

        for(DataTagValue t : myDigitalSignals){
            if(t.getName().equals(sUmfeldvariable)){
                return  t;
            }
        }
         
        return null;
    }
 
    public void remove(DataTagValue umfelddatum)
    {
        myDigitalSignals.remove(umfelddatum);
        myAnalogSignals.remove(umfelddatum);
    }




    @Override
    public String toString(){
        return this.name;
    }
}
