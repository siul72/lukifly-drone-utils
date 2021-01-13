package co.luism.ksoft.iot.utils.enterprise;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by luis on 15.10.14.
 */
public class DataBuffer {


    private int bufferID;
    private int size;
    private int intervall_s;
    private int intervall_ms;
    private Map<String, Category> kategorien;


    public DataBuffer(int iID, int size, int intervall_s, int intervall_ms)
    {
        this.bufferID = iID;
        this.size = size;
        this.intervall_s = intervall_s;
        this.intervall_ms = intervall_ms;
        this.kategorien = new HashMap<>();
    }


    public int getBufferID() {
        return bufferID;
    }

    public void setBufferID(int bufferID) {
        this.bufferID = bufferID;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getIntervall_s() {
        return intervall_s;
    }

    public void setIntervall_s(int intervall_s) {
        this.intervall_s = intervall_s;
    }

    public int getIntervall_ms() {
        return intervall_ms;
    }

    public void setIntervall_ms(int intervall_ms) {
        this.intervall_ms = intervall_ms;
    }

    public Map<String, Category> getKategorien() {
        return kategorien;
    }

    public void setKategorien(Map<String, Category> kategorien) {
        this.kategorien = kategorien;
    }

    public boolean addKategorie(Category addKategorie)
    {
        if(this.kategorien.containsKey(addKategorie.getName())){

            return false;
        }

        this.kategorien.put(addKategorie.getName(), addKategorie);

        return true;
    }


    public void removeKategorie(Category removeKategorie)
    {
        kategorien.remove(removeKategorie);
    }


    public boolean containsKategorie(Category containKategorie)
    {
        if(kategorien.containsKey(containKategorie.getName())){
            return true;
        }
        return false;
    }

    public int getIntervallInMillis()
    {
        return (1000 * intervall_s) + intervall_ms;
    }

    @Override
    public String toString()
    {
        return ("Buffer " + this.bufferID + "  (" + this.intervall_s + " [s], " + this.intervall_ms + " [ms], " + this.size + ")");
    }
}
