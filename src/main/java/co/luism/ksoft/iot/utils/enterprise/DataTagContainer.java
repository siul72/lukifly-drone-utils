package co.luism.ksoft.iot.utils.enterprise;

import java.util.*;

/**
 * Created by luis on 15.10.14.
 */
public class DataTagContainer {

    /** EKE Umfelddaten */
    private Set<DataTagEke> umfeldEKE;

    /**
     * Konstruktor
     */
    public DataTagContainer()
    {
        umfeldEKE = new TreeSet<>();
    }

    /**
     * Umfeldwert in den entsprechenden Vector aufnehmen. Das Hinzufügen geschieht durch eine
     * überprüfung mit instanceof
     *
     * @param neuesUmfelddatum Neuer Umfeldwert vom Typ hinzufügen
     */
    public void addUmfeldwert(DataTagValue neuesUmfelddatum)
    {
        if(neuesUmfelddatum instanceof DataTagEke){
            umfeldEKE.add((DataTagEke)neuesUmfelddatum);
        }

    }

    /**
     * Umfelddatum aus dem entsprechenden Vector löschen. Das Löschen geschieht durch eine
     * überprüfung mit instanceof
     *
     * @param loeschUmfelddatum
     */
    public void removeUmfeldwert(DataTagValue loeschUmfelddatum)
    {
        if(loeschUmfelddatum instanceof DataTagEke){
            umfeldEKE.remove(loeschUmfelddatum);
        }
    }

    public Set<DataTagEke> getEKEVector()
    {
        return umfeldEKE;
    }

    /**
     * Referenz auf ein Umfelddatum erhalten mit dem Namen des Umfelddatums
     *
     * @param sName Name des Umfelddatums
     * @return Referenz auf das Umfelddatum
     */
    public DataTagValue getUmfelddatum(String sName)
    {
        Iterator it = umfeldEKE.iterator();
        while(it.hasNext()){
            DataTagValue temp = (DataTagValue)it.next();

            if(temp.getName().equals(sName)){
                return temp;
            }
        }

        return null;
    }

    public int getAnzahlUmfelddaten()
    {
        int iAnzahl = 0;
        iAnzahl += umfeldEKE.size();

        return iAnzahl;
    }

}
