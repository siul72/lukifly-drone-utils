package co.luism.ksoft.iot.utils.configuration;

import co.luism.ksoft.iot.utils.common.CognitioUtils;
import co.luism.ksoft.iot.utils.common.HardwareEnum;
import co.luism.ksoft.iot.utils.enterprise.Category;
import co.luism.ksoft.iot.utils.enterprise.DataBuffer;
import co.luism.ksoft.iot.utils.enterprise.DataTagContainer;
import co.luism.ksoft.iot.utils.enterprise.Fahrzeug;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by luis on 15.10.14.
 */
public class BaseConfigurationManager
{
    private static final Logger LOG = Logger.getLogger(BaseConfigurationManager.class);
    private String version = "";
    private HardwareEnum hardware;
    private String projekt;
    private String projekthash;
    private int serverPort = 4566;
    private int fehlerAbtast = 100;
    private int fehlerSize = 2;
    private boolean ringbuffer = true;
    private String[] sprache = {"", "", ""};
    private boolean werkstattText = false;

    private final List<Fahrzeug> fahrzeugList = new ArrayList<>();
    private final List<DataBuffer> dataBuffers = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private final DataTagContainer dataTagContainer = new DataTagContainer();
    private EnumEndianess byteOrder = EnumEndianess.BIG_ENDIAN;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HardwareEnum getHardware() {
        return hardware;
    }

    public void setHardware(HardwareEnum cnf_hardware) {
        this.hardware = cnf_hardware;

//        if(getHardware().equals("EKE Rechner")){
//
//            setByteOrder(EnumEndianess.LITLE_ENDIAN);
//
//        } else {
//
//            setByteOrder(EnumEndianess.BIG_ENDIAN);
//        }

    }

    public String getProjekt() {
        return projekt;
    }

    public void setProjekt(String projekt) {
        this.projekt = projekt;
    }

    public String getProjekthash() {
        if(this.projekthash == null){
            this.projekthash = CognitioUtils.getMD5Hash(this.projekt + this.version + this.hardware.getValue());
        }

        return projekthash;
    }

    public String createProjekthash() {
        if(this.projekthash == null){
            this.projekthash = CognitioUtils.getMD5Hash(this.projekt + this.version + this.hardware.getValue());
        }

        return projekthash;
    }

    public void setProjekthash(String projekthash) {
        this.projekthash = projekthash;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getFehlerAbtast() {
        return fehlerAbtast;
    }

    public void setFehlerAbtast(int fehlerAbtast) {
        this.fehlerAbtast = fehlerAbtast;
    }

    public int getFehlerSize() {
        return fehlerSize;
    }

    public void setFehlerSize(int fehlerSize) {
        this.fehlerSize = fehlerSize;
    }

    public boolean isRingbuffer() {
        return ringbuffer;
    }

    public void setRingbuffer(boolean ringbuffer) {
        this.ringbuffer = ringbuffer;
    }

    public String[] getSprache() {
        return sprache;
    }

    public void setSprache(String[] sprache) {
        this.sprache = sprache;
    }

    public boolean isWerkstattText() {
        return werkstattText;
    }

    public void setWerkstattText(boolean werkstattText) {
        this.werkstattText = werkstattText;
    }

    public List<Fahrzeug> getFahrzeugList() {
        return fahrzeugList;
    }

    public List<DataBuffer> getDataBuffers() {
        return dataBuffers;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public DataTagContainer getDataTagContainer() {
        return dataTagContainer;
    }

    public void removeFahrzeug(int iIndex)
    {
        if(iIndex >= 0){
            fahrzeugList.remove(iIndex);
        }
    }


    public boolean addBuffer(int i_sek, int i_ms, int i_size)
    {
        if(dataBuffers.size() < UtilsConfiguration.MAXBUFFER){
            dataBuffers.add(new DataBuffer(dataBuffers.size(), i_size, i_sek, i_ms));
            return true;
        }else{
            return false;
        }
    }

    public boolean removeBuffer(int iVectorPos)
    {

        dataBuffers.remove(iVectorPos);
        return true;
    }


    public int[] calcStartOfKategorien()
    {

        int iStart[] = new int[categories.size()];
        for(int i=0; i< categories.size(); i++){
            iStart[i] = 0;
        }

        int iBufferLast[] = new int[dataBuffers.size()];
        for(int i=0; i< dataBuffers.size(); i++){
            iBufferLast[i] = 0;
        }

        Iterator itBuffer = dataBuffers.iterator();
        while(itBuffer.hasNext()){
            DataBuffer tempBuffer = (DataBuffer)itBuffer.next();

            for(Category c : tempBuffer.getKategorien().values()){
                int iBuffer = tempBuffer.getBufferID();
                iStart[categories.indexOf(c)] = iBufferLast[iBuffer];
                iBufferLast[iBuffer] += c.getSizeInBytes();

            }


        }

        return iStart;
    }


    public EnumEndianess getByteOrder()
    {
        LOG.debug(String.format("Get Byte order %s:%d", byteOrder, byteOrder.ordinal()));
        return byteOrder;

    }


    public void setByteOrder(EnumEndianess byteOrder) {
        LOG.debug(String.format("Byte order is set to %s (0=Litle-endian 1=Big-endian)", byteOrder));
        this.byteOrder = byteOrder;
    }

    private int getKategoriePosition(Category tempKategorie)
    {
        return categories.indexOf(tempKategorie);
    }

}

