package co.luism.ksoft.iot.utils.enterprise;

import co.luism.ksoft.iot.utils.common.UICCategoryEnum;
import co.luism.ksoft.iot.utils.configuration.UtilsConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by luis on 15.10.14.
 */
public class DataTagEvent implements Comparable{

    private final Integer id;
    private String name = "";
    private String referenz = "";
    private boolean react = false;
    private boolean preHistory = false;
    private boolean postHistory = false;
    private boolean xAlarm = false;
    private String kurztext[];
    private String text[][];
    /** UIC Kategorie A, A1, B, B1, C, M */
    private UICCategoryEnum uICKategorie = UICCategoryEnum.NONE;
    private int maxAuftreten = 0;
    private Set<Category> kategorien;

    public DataTagEvent(Integer id, String sUmfeldname, String sReferenz, UICCategoryEnum sUICKategorie, boolean xReact, boolean iPre, boolean iPost, int iMaxAuftreten)
    {
        this(id, sUmfeldname);
        this.referenz = sReferenz;
        this.uICKategorie = sUICKategorie;
        this.react = xReact;
        this.preHistory = iPre;
        this.postHistory = iPost;
        this.maxAuftreten = iMaxAuftreten;
    }


    public DataTagEvent(Integer id, String sUmfeldname)
    {
        this.text = new String[UtilsConfiguration.MAX_LANGUAGES][3];
        this.kurztext = new String[UtilsConfiguration.MAX_LANGUAGES];
        kategorien = new HashSet<>();

        for(int i = 0; i < UtilsConfiguration.MAX_LANGUAGES; i++){
            this.kurztext[i] = new String("");
            for(int j = 0; j < 3; j++){
                this.text[i][j] = new String("");
            }
        }

        this.name = sUmfeldname;
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferenz() {
        return referenz;
    }

    public void setReferenz(String referenz) {
        this.referenz = referenz;
    }

    public boolean isReact() {
        return react;
    }

    public void setReact(boolean react) {
        this.react = react;
    }

    public boolean isPreHistory() {
        return preHistory;
    }

    public void setPreHistory(boolean preHistory) {
        this.preHistory = preHistory;
    }

    public boolean isPostHistory() {
        return postHistory;
    }

    public void setPostHistory(boolean postHistory) {
        this.postHistory = postHistory;
    }

    public boolean isxAlarm() {
        return xAlarm;
    }

    public void setxAlarm(boolean xAlarm) {
        this.xAlarm = xAlarm;
    }

    public String[] getKurztext() {
        return kurztext;
    }

    public void setKurztext(String[] kurztext) {
        this.kurztext = kurztext;
    }

    public String[][] getText() {
        return text;
    }

    public void setText(String[][] text) {
        this.text = text;
    }

    public UICCategoryEnum getuICKategorie() {
        return uICKategorie;
    }

    public void setuICKategorie(UICCategoryEnum uICKategorie) {
        this.uICKategorie = uICKategorie;
    }

    public int getMaxAuftreten() {
        return maxAuftreten;
    }

    public void setMaxAuftreten(int maxAuftreten) {
        this.maxAuftreten = maxAuftreten;
    }

    public Set<Category> getKategorien() {
        return kategorien;
    }

    public void setKategorien(Set<Category> kategorien) {
        this.kategorien = kategorien;
    }

    @Override
    public int compareTo(Object o) {

        if(o instanceof DataTagEvent){
            return this.getName().compareTo(((DataTagEvent)o).getName());
        }

        return 0;
    }

    public Integer getId() {
        return id;
    }
}
