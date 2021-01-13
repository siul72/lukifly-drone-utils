package co.luism.ksoft.iot.utils.configuration;

import co.luism.ksoft.iot.utils.common.CognitioTypeEnum;
import co.luism.ksoft.iot.utils.common.HardwareEnum;
import co.luism.ksoft.iot.utils.common.UICCategoryEnum;
import co.luism.ksoft.iot.utils.common.UnitsEnum;
import co.luism.ksoft.iot.utils.enterprise.*;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by luis on 15.10.14.
 */
public class ConfigurationManagerUtils {

    private static final Logger LOG = Logger.getLogger(ConfigurationManagerUtils.class);
    private static SpreadSheet spreadsheet;


    public static ReturnCode loadProjectPropertiesFromODS(BaseConfigurationManager cnf, File openFile)
    {

        Sheet mySheet;
        try {
            spreadsheet = SpreadSheet.createFromFile(openFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(spreadsheet == null){

            return ReturnCode.ODS_TITLE_NOT_FOUND;
        }

        mySheet = spreadsheet.getSheet(UtilsConfiguration.ODS_TITLE);

        if(mySheet == null){
            return ReturnCode.ODS_TITLE_NOT_FOUND;
        }

        ReturnCode ret = getTitleInfoToConfiguration(cnf, mySheet);

        if(ret != ReturnCode.OK){
            return ret;
        }

        mySheet = spreadsheet.getSheet(UtilsConfiguration.ODS_BUFFER);
        if(mySheet == null){
            return ReturnCode.ODS_BUFFER_NOT_FOUND;
        }

        ret = getBufferConfiguration(cnf, mySheet);
        if(ret != ReturnCode.OK){
            return ret;
        }

        mySheet = spreadsheet.getSheet(UtilsConfiguration.ODS_FAHRZEUGE);
        if(mySheet == null){
            return ReturnCode.ODS_FAHRZEUGE_NOT_FOUND;
        }

        getVehicleConfiguration(cnf, mySheet);

        mySheet = spreadsheet.getSheet(UtilsConfiguration.ODS_KATEGORIEN);
        if(mySheet == null){
            return ReturnCode.ODS_KATEGORIEN_NOT_FOUND;
        }

        getCategoryConfiguration(cnf, mySheet);

        mySheet = spreadsheet.getSheet(UtilsConfiguration.ODS_UMFELD);
        if(mySheet != null){
            ret = getDataTagConfiguration(cnf, mySheet);
            if(ret != ReturnCode.OK){
                return ret;
            }
        }

        mySheet = spreadsheet.getSheet(UtilsConfiguration.ODS_EREIGNISSE);
        if(mySheet != null){
            ret = getEventsConfiguration(cnf, mySheet);
            if(ret != ReturnCode.OK){
                return ret;
            }
        }


        try {
            spreadsheet.saveAs(openFile);
            return ReturnCode.OK;
        }catch (IOException ex){
            LOG.error(ex);
             return ReturnCode.NOK;
        }

    }


    private static ReturnCode getEventsConfiguration(BaseConfigurationManager cnf, Sheet sheetEvents) {

        for(int i = 7; i < sheetEvents.getRowCount(); i++){
            Object oName = sheetEvents.getValueAt(1, i);
            if(oName instanceof String && !oName.equals("")){
                DataTagValue tempUmfelddatum = cnf.getDataTagContainer().getUmfelddatum((String) oName);
                if(tempUmfelddatum != null){
                    tempUmfelddatum.setEreignis(true);
                    DataTagEvent tempEreignis = new DataTagEvent(i-7, (String)oName);

                    Object oText = sheetEvents.getValueAt(0, i);
                    String refnum = oText.toString();
                    tempEreignis.setReferenz(refnum);
//                            if(oText instanceof String){
//                                tempEreignis.setReferenz((String)oText);
//                            }

                    oText = sheetEvents.getValueAt(2, i);
                    if(oText instanceof String){
                        UICCategoryEnum u = UICCategoryEnum.valueOf((String)oText);
                        tempEreignis.setuICKategorie(u);
                    }

                    oText = sheetEvents.getValueAt(3, i);
                    if(oText instanceof String) {
                        if (((String) oText).toLowerCase().equals("x")) {
                            tempEreignis.setReact(true);
                        }
                    }

                    if(tempEreignis.isReact()){

                        oText = sheetEvents.getValueAt(4, i);
                        if(oText instanceof String){
                            if(((String)oText).toLowerCase().equals("x")){
                                tempEreignis.setPreHistory(true);
                            }
                        }

                        oText = sheetEvents.getValueAt(5, i);
                        if(oText instanceof String){
                            if(((String)oText).toLowerCase().equals("x")){
                                tempEreignis.setPostHistory(true);
                            }
                        }

                    }

                    oText = sheetEvents.getValueAt(6, i);
                    if(oText instanceof Float){
                        float fAnzahl = (Float)oText;
                        tempEreignis.setMaxAuftreten((int)fAnzahl);
                    }else{
                        tempEreignis.setMaxAuftreten(0);
                    }


                    for(int j=0; j<3; j++){


                        // Kurztext
                        oText = sheetEvents.getValueAt(7 + (j*4), i);
                        if(oText instanceof String){
                            tempEreignis.getKurztext()[j] = (String)oText;
                        }


                        oText = sheetEvents.getValueAt(8 + (j*4), i);
                        if(oText instanceof String){

                            tempEreignis.getText()[j][0] = (String)oText;

                        }

                        oText = sheetEvents.getValueAt(9 + (j*4), i);
                        if(oText instanceof String){
                            tempEreignis.getText()[j][1] = (String)oText;
                        }

                        if(cnf.isWerkstattText()){
                            oText = sheetEvents.getValueAt(10 + (j*4), i);
                            if(oText instanceof String){
                                tempEreignis.getText()[j][2] = (String)oText;
                            }
                        }
                    }

                    getTranslation(tempEreignis, UtilsConfiguration.ODS_EN_TRANSLATION, i , 3);




                    Object oTemp;
                    for(int k = 0; k < cnf.getCategories().size(); k++){
                        oTemp = sheetEvents.getValueAt(k + 19, i);
                        if(oTemp instanceof String){
                            if(((String)oTemp).toLowerCase().equals("x")){
                                tempEreignis.getKategorien().add(cnf.getCategories().get(k));
                                //tempUmfelddatum.getEvent().getKategorien().add(cnf.getCategories().get(k));
                            }
                        }
                    }

                    tempUmfelddatum.setEvent(tempEreignis);

                }else{

                    return ReturnCode.ODS_EVENT_NOT_EXIST;
                }
            }
        }

        return ReturnCode.OK;
    }

    private static void getTranslation(DataTagEvent tempEreignis, String sheetName,Integer line, Integer index) {

        Sheet mySheet = spreadsheet.getSheet(sheetName);
        if(mySheet == null){
            return;
        }

        if(line >= mySheet.getRowCount()){
            LOG.warn("no translation found at line " + line + " for " + sheetName);
            return;
        }

        // Kurztext
        Object oText = mySheet.getValueAt(0, line);
        if(oText instanceof String){
            tempEreignis.getKurztext()[index] = (String)oText;
        }


        oText = mySheet.getValueAt(1, line);
        if(oText instanceof String){

            tempEreignis.getText()[index][0] = (String)oText;

        }

        oText = mySheet.getValueAt(2, line);
        if(oText instanceof String){
            tempEreignis.getText()[index][1] = (String)oText;
        }


        oText = mySheet.getValueAt(3, line);
        if(oText instanceof String){
            tempEreignis.getText()[index][2] = (String)oText;
        }

    }

    private static ReturnCode getDataTagConfiguration(BaseConfigurationManager cnf, Sheet sheetDataTags) {

        for(int i = 7; i < sheetDataTags.getRowCount(); i++){
            Object oName = sheetDataTags.getValueAt(0, i);
            Object oKommentar = sheetDataTags.getValueAt(1, i);
            Object oDatentyp = sheetDataTags.getValueAt(2, i);

            if((oName instanceof String) && !oName.equals("") && (oKommentar instanceof String) && (oDatentyp instanceof String)){
                String sDatentyp = (String)oDatentyp;
                DataTagEke tempEKE = null;

                if(sDatentyp.equals(CognitioTypeEnum.TYP_BIT.getValue())) {
                    tempEKE = new DataTagEke(i - 7, (String) oName, (String) oKommentar, UnitsEnum.UNIT_NONE, CognitioTypeEnum.TYP_BIT, "", false, false);
                    cnf.getDataTagContainer().getEKEVector().add(tempEKE);

                    for (int j = 0; j < cnf.getCategories().size(); j++) {
                        Object oKat = sheetDataTags.getValueAt(j + 6, i);
                        if (oKat instanceof String) {
                            if (((String) oKat).toLowerCase().equals("x")) {
                                cnf.getCategories().get(j).getMyDigitalSignals().add(tempEKE);
                            }
                        }
                    }
                } else {
                    CognitioTypeEnum t = CognitioTypeEnum.getEnum(sDatentyp);
                    if(t == null){
                        LOG.error("Enum not found");
                        return ReturnCode.NOK;
                    }
                    ReturnCode ret = ConfigurationManagerUtils.readEnvDataODS(cnf, sheetDataTags, i, (String)oName, (String)oKommentar, t);
                    if(ret != ReturnCode.OK){
                        return ret;
                    }


                }

            } else{
                continue;
            }
        }

        return ReturnCode.OK;
    }

    private static void getCategoryConfiguration(BaseConfigurationManager cnf, Sheet sheetKategorien) {
        // Kategorieinformationen
        for(int i = 0; i < UtilsConfiguration.MAXKATEGORIEN; i++){
            Object oKatId = sheetKategorien.getCellAt(0, 6 + i).getValue();
            Object oKatName = sheetKategorien.getCellAt(1, 6 + i).getValue();
            Object oBuffer = sheetKategorien.getCellAt(2, 6 + i).getValue();
            if((oKatName instanceof String) && (!(oKatName).equals(""))){
                if(oBuffer instanceof Float){
                    int iBufferID = (int)((Float)oBuffer * 1);
                    iBufferID = iBufferID -1;
                    if(iBufferID < UtilsConfiguration.MAXBUFFER){

                        if(iBufferID > cnf.getDataBuffers().size()){
                            LOG.error(String.format(
                                    "The buffer index is bigger then the maximum %d:%d",
                                    iBufferID, cnf.getDataBuffers().size()));
                            continue;
                        }

                        if(cnf.getCategories().size() > UtilsConfiguration.MAXKATEGORIEN){
                            LOG.error(String.format(
                                    "Max categories reached %d",
                                    UtilsConfiguration.MAXKATEGORIEN));
                            continue;
                        }

                        Integer id;
                        if(oKatId instanceof Float){

                            id = Math.round((Float)oKatId);

                        } else {
                            id = i;
                        }


                        Category myCategory = new Category(id, (String)oKatName);
                        DataBuffer tempBuffer = cnf.getDataBuffers().get(iBufferID);
                        if(tempBuffer != null){
                            myCategory.setBuffer(tempBuffer);
                            tempBuffer.addKategorie(myCategory);
                            cnf.getCategories().add(myCategory);
                        } else {
                            LOG.error("Buffer not found for index " + iBufferID);
                        }

                    }
                }
            }
        }
    }

    private static ReturnCode getBufferConfiguration(BaseConfigurationManager cnf, Sheet sheetBuffer) {

        for(int i = 0; i < UtilsConfiguration.MAXBUFFER; i++){
            Object oIntervall = sheetBuffer.getValueAt(1, 6 + i);
            Object oSize = sheetBuffer.getValueAt(2, 6 + i);
            if((oIntervall instanceof Float) && (oSize instanceof Float)){
                int sampleRateMilliSeconds = (int)((Float)oIntervall * 1000);
                int numberOfSamples = (int)Math.round((Float)oSize);

                if(numberOfSamples > UtilsConfiguration.MAX_NUMBER_OF_SAMPLES){
                    numberOfSamples = UtilsConfiguration.MAX_NUMBER_OF_SAMPLES;
                }

                if(cnf.getDataBuffers().size() >= UtilsConfiguration.MAXBUFFER){
                    return ReturnCode.ODS_MAXBUFFER_SIZE_REACHED;
                }

                if(sampleRateMilliSeconds < UtilsConfiguration.MIN_BUFFER_SAMPLE_RATE){
                    LOG.warn("sample rate was change to 100ms");
                    sampleRateMilliSeconds = UtilsConfiguration.MIN_BUFFER_SAMPLE_RATE;

                }

                if((sampleRateMilliSeconds * numberOfSamples) > UtilsConfiguration.MAX_BUFFER_MS_TIME){
                    numberOfSamples = UtilsConfiguration.MAX_BUFFER_MS_TIME / sampleRateMilliSeconds;
                    LOG.warn("number of samples was change to " + numberOfSamples);
                }

                //public DataBuffer(int iID, int size, int intervall_s, int intervall_ms)
                DataBuffer b = new DataBuffer(cnf.getDataBuffers().size(), numberOfSamples, sampleRateMilliSeconds/1000, sampleRateMilliSeconds % 1000);
                cnf.getDataBuffers().add(b);
            }
        }

        return ReturnCode.OK;
    }

    private static ReturnCode  getTitleInfoToConfiguration(BaseConfigurationManager cnf, Sheet sheetTitle) {

        String sTemp = (String)sheetTitle.getValueAt(2, 5);
        if(!sTemp.equals("rts_config")){
            return ReturnCode.RTS_CONFIG_NOT_FOUND;
        }

        String []lang = new String[3];

        if((sTemp = (String)sheetTitle.getValueAt(2, 19)) != null){
            lang[0] = sTemp;
        }

        if((sTemp = (String)sheetTitle.getValueAt(2, 20)) != null){
            lang[1] = sTemp;
        }

        if((sTemp = (String)sheetTitle.getValueAt(2, 21)) != null){
            lang[2] = sTemp;
        }

        cnf.setSprache(lang);

        if((sTemp = (String)sheetTitle.getValueAt(2, 23)) != null){
            sTemp = sTemp.toLowerCase();
            Boolean dummy = (sTemp.equals("true")) ? true : false;
            cnf.setWerkstattText(dummy);

        } else {
            return ReturnCode.ODS_NO_Werkstatttext;

        }


        // Version
        if((sTemp = (String)sheetTitle.getValueAt(2, 26)) != null){
            cnf.setVersion(sTemp);
        }

        // Hardware
        if((sTemp = (String)sheetTitle.getValueAt(2, 27)) != null){

            LOG.debug(String.format("get hw %s", sTemp));
            HardwareEnum h = HardwareEnum.getByName(sTemp);

            if(h == null){
                return ReturnCode.ODS_WRONG_HARDWARE_TYPE;
            }

            cnf.setHardware(h);

        }


        if((sTemp = (String)sheetTitle.getValueAt(2, 28)) != null){
            cnf.setProjekt(sTemp);
        }

        sTemp = (String)sheetTitle.getValueAt(2, 29);
        if(sTemp != null && !sTemp.equals("")){
            cnf.setProjekthash(sTemp);
        } else {
           sheetTitle.setValueAt(cnf.createProjekthash(), 2, 29);
        }

        sTemp = (String)sheetTitle.getValueAt(2, 30);
        if(sTemp != null && !sTemp.equals("")){
            EnumEndianess e = EnumEndianess.getEnum(sTemp);

            if(e != null){
                cnf.setByteOrder(e);
            }

        } else {

            sheetTitle.setValueAt(cnf.getByteOrder(), 2, 30);
        }

        if((sTemp = (String)sheetTitle.getValueAt(2, 31)) != null){
            try{
                cnf.setFehlerSize(Integer.parseInt(sTemp));
            }catch(NumberFormatException ex){

                return ReturnCode.ODS_LOG_SIZE_NOT_PARSABLE;
            }
        }

        if((sTemp = (String)sheetTitle.getValueAt(2, 32)) != null){
            try{
                cnf.setFehlerAbtast(Integer.parseInt(sTemp));
            }catch(NumberFormatException ex){

                return ReturnCode.ODS_SAMPLING_INTERVAL_NOT_PARSABLE;
            }
        }

        if((sTemp = (String)sheetTitle.getValueAt(2, 33)) != null){
            sTemp = sTemp.toLowerCase();
            Boolean b = (sTemp.equals("true")) ? true : false;
            cnf.setRingbuffer(b);

        }else {

            return ReturnCode.ODS_RINGBUFFER_SETTING_NOT_READ;
        }


        if((sTemp = (String)sheetTitle.getValueAt(2, 35)) != null){
            try{
                cnf.setServerPort(Integer.parseInt(sTemp));
            }catch(NumberFormatException ex){

                return ReturnCode.ODS_SERVER_PORT_NOT_PARSABLE;
            }
        }


        return ReturnCode.OK;
    }

    private static void getVehicleConfiguration(BaseConfigurationManager cnf, Sheet sheetFahrzeuge) {
        for(int i = 6; i < sheetFahrzeuge.getRowCount(); i++){
            String oFahrzeugname = sheetFahrzeuge.getValueAt(0, i).toString();
            String oEinbauort = sheetFahrzeuge.getValueAt(1, i).toString();
            String hashCode = sheetFahrzeuge.getValueAt(2, i).toString();

            if(hashCode.length() >= 32){
                Fahrzeug tempFahrzeug = new Fahrzeug(oFahrzeugname, oEinbauort, hashCode);
                cnf.getFahrzeugList().add(tempFahrzeug);

            } else {
                if(!(oFahrzeugname).equals("")){
                    Fahrzeug tempFahrzeug = new Fahrzeug(oFahrzeugname, oEinbauort);
                    cnf.getFahrzeugList().add(tempFahrzeug);
                    sheetFahrzeuge.setValueAt(tempFahrzeug.getMD5Hash(), 2, i);
                } else {
                    LOG.info(String.format("no vehicle on sheet %s, row %d", UtilsConfiguration.ODS_FAHRZEUGE ,i));
                }

            }
        }
    }

    /**
     * XML Datei analysieren und Konfiguration aufbauen. Anhand der Root Element
     * werden die entsprechenden Funktionen aufgerufen, welche das weitere
     * einlesen übernehmen.
     *
     * @param konfigFile
     */
    public static ReturnCode loadProjectPropertiesFromXML(BaseConfigurationManager cnf, File konfigFile)
    {
        try{
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(konfigFile);

            // Root Element
            Element root = doc.getRootElement();

            List myList = root.getChildren();

            for(Object element : myList){

                if(element instanceof Element){

                    switch(((Element)element).getName()){
                        case UtilsConfiguration.XML_BUFFER_ROOT:
                            readBufferXML(cnf, (Element)element);
                        break;

                        case UtilsConfiguration.XML_UMF_ROOT:
                             readUmfelddatenXML(cnf, (Element)element);
                        break;

                        case UtilsConfiguration.XML_PROP:
                            readPropertiesXML(cnf, (Element)element);
                        break;

                        case UtilsConfiguration.XML_LOG:
                            readLoggerXML(cnf, (Element)element);
                        break;
                        case UtilsConfiguration.XML_VEHICLE_ROOT:
                            readFahrzeugeXML(cnf, (Element)element);
                        break;
                        case UtilsConfiguration.XML_DISP:
                            readDisplayXML(cnf, (Element)element);
                        break;
                        case UtilsConfiguration.XML_UMFKAT_ROOT:
                            readKategorienXML(cnf, (Element)element);
                        break;
                        case UtilsConfiguration.XML_EREIG_ROOT:
                            readEreignisXML(cnf, (Element)element);
                        break;

                        default:
                        break;
                    }


                }


            }
        }catch (JDOMException exc){
            LOG.error(exc);
            return ReturnCode.XML_ERROR_READ_DOM;
        }catch (IOException exc){
            LOG.error(exc);
            return ReturnCode.XML_FILE_NOT_FOUND;
        }

        return ReturnCode.OK;
    }

    /**
     * Einstellungen für das Display und dessen Sprachen
     * - Sprache1
     * - Sprache2
     * - Sprache3
     * - Informations- oder Werkstatttext
     *
     * @param element XML Root Element für Displayinformationen
     */
    private static ReturnCode readDisplayXML(BaseConfigurationManager cnf, Element element)
    {
        String sTemp = "";

        for(int i = 0 ; i < 3; i++){
            sTemp = element.getChildText(UtilsConfiguration.XML_DISP_LAN[i]);
            if(sTemp != null){
                cnf.getSprache()[i] = sTemp;
            }
        }

        sTemp = element.getChildText(UtilsConfiguration.XML_DISP_INFO);
        if(sTemp != null){
            if(sTemp.toLowerCase().equals("true")){
                cnf.setWerkstattText(true);
            }else{
                cnf.setWerkstattText(false);
            }
        }else{
            cnf.setWerkstattText(false);
        }


        return ReturnCode.OK;
    }

    /**
     * Properties der Konfiguration einlesen
     * - Versionsbeschreibug
     * - Hardwarebeschreibug (Falls diese nicht erkannt wird, wird der Rest
     * der Konfiguration nicht eingelesen)
     * - Projektbeschreibung
     * - Serverport
     *
     * @param element XML Root Element für Properties
     * @return
     *      true -> Elemente richtig eingelsen oder Defaultwerte gesetzt
     *      false -> Hardware konnte nicht gefunden werden
     */
    private static ReturnCode readPropertiesXML(BaseConfigurationManager cnf, Element element)
    {
        String sTemp = "";

        sTemp = element.getChildText(UtilsConfiguration.XML_PROP_PROJECT_HASH);
        if(sTemp != null){
            cnf.setProjekthash(sTemp);
        }

        sTemp = element.getChildText(UtilsConfiguration.XML_PROP_DATA_ENDIANESS);
        if(sTemp != null){
            EnumEndianess e = EnumEndianess.getEnum(sTemp);
            if(e != null){
                cnf.setByteOrder(e);
            }
        }

        sTemp = element.getChildText(UtilsConfiguration.XML_PROP_VER);
        if(sTemp != null){
            cnf.setVersion(sTemp);
        }

        sTemp = element.getChildText(UtilsConfiguration.XML_PROP_PROJEKT);
        if(sTemp != null){
            cnf.setProjekt(sTemp);
        }

        sTemp = element.getChildText(UtilsConfiguration.XML_PROP_SERVER);
        if(sTemp != null){
            try{
                cnf.setServerPort(Integer.parseInt(sTemp));
            }catch(NumberFormatException exc){
                return ReturnCode.XML_PORT_NUMBER_FORMAT_EXCEPTION;
            }
        }

        sTemp = element.getChildText(UtilsConfiguration.XML_PROP_HARDWARE);

        HardwareEnum h = HardwareEnum.getByName(sTemp);

        if(h == null){
            return ReturnCode.XML_HARDWARE_TYPE_NOT_FOUND;
        }
        cnf.setHardware(h);
        return ReturnCode.OK;
    }

    /**
     * Loggerinformationen einlesen
     * - Grösse der err.log Datei
     * - Abtastinterval
     * - Ringbuffermodus oder Normalmodus
     *
     * Falls beim Parsen eines Elements ein Fehler auftritt, wird das einlesen
     * der Konfiguration gestoppt.
     *
     * @param element XML Root Element für Loggerinformationen
     * @return
     */
    private static ReturnCode readLoggerXML(BaseConfigurationManager cnf, Element element)
    {
        String sTemp = "";

        try{
            sTemp = element.getAttributeValue(UtilsConfiguration.XML_LOG_SIZE);
            if(sTemp != null && !sTemp.equals("")){
                cnf.setFehlerSize(Integer.parseInt(sTemp));
            }

            sTemp = element.getAttributeValue(UtilsConfiguration.XML_LOG_INTERVAL);
            if(sTemp != null && !sTemp.equals("")){
                cnf.setFehlerAbtast(Integer.parseInt(sTemp));
            }

            sTemp = element.getAttributeValue(UtilsConfiguration.XML_LOG_RING);
            if(sTemp != null && !sTemp.equals("")){
                if(sTemp.equals("true")){
                    cnf.setRingbuffer(true);
                }else if(sTemp.equals("false")){
                    cnf.setRingbuffer(false);
                }
            }
        }catch(NumberFormatException exc){

            return ReturnCode.XML_READ_LOGGER_INVALID_NUMERIC_FORMAT;
        }

        return ReturnCode.OK;
    }

    private static ReturnCode readFahrzeugeXML(BaseConfigurationManager cnf, Element element)
    {
        Element fahrzeugNode;

        List myList = element.getChildren();
        for(int i = 0; i < myList.size(); i++){
            fahrzeugNode = (Element)myList.get(i);
            Attribute att = fahrzeugNode.getAttribute(UtilsConfiguration.XML_VEHICLE_MD5);
            Fahrzeug neuesFahrzeug = new Fahrzeug(fahrzeugNode.getChildText(UtilsConfiguration.XML_VEHICLE_NAME),
                    fahrzeugNode.getChildText(UtilsConfiguration.XML_VEHICLE_POS),
                    att.getValue());
            cnf.getFahrzeugList().add(neuesFahrzeug);
        }

        return ReturnCode.OK;
    }


    private static ReturnCode readBufferXML(BaseConfigurationManager cnf, Element element)
    {
        Element bufferElement;

        List myList = element.getChildren();
        for(int i = 0; i < myList.size(); i++){
            bufferElement = (Element)myList.get(i);

            if(bufferElement.getName().equals(UtilsConfiguration.XML_BUFFER)){
                int sek = 0;
                int ms = 0;
                int size = 0;

                try{
                    sek = Integer.parseInt(bufferElement.getAttributeValue(UtilsConfiguration.XML_BUFFER_S));
                    ms = Integer.parseInt(bufferElement.getAttributeValue(UtilsConfiguration.XML_BUFFER_MS));
                    size = Integer.parseInt(bufferElement.getAttributeValue(UtilsConfiguration.XML_BUFFER_SIZE));
                }catch(NumberFormatException exc){
                     return ReturnCode.XML_BUFFER_PARAMETERS_NUMBER_FORMAT_ERROR;
                }

                cnf.addBuffer(sek, ms, size);

            }
        }

        return ReturnCode.OK;
    }

    /**
     * Einlesen eines Umfeldwertes. Dieser wird abhängig vom jeweiligen Interface
     * eingelesen und ausgewertet.
     *
     * @param element XML Root Element für Umfelddaten
     */
    public static ReturnCode readUmfelddatenXML(BaseConfigurationManager cnf, Element element)
    {
        Element interfaceNode, signalNode;
        String sInterface = "";

        // Interfaceschlaufe wenn mehrere Interfaces zum Einsatz kommen
        List myList1 = element.getChildren();
        for(int i=0; i<myList1.size(); i++){
            interfaceNode = (Element)myList1.get(i);
            sInterface = interfaceNode.getName();

            // Signalschlaufe
            List myList2 = interfaceNode.getChildren();
            for(int j=0; j<myList2.size(); j++){
                signalNode = (Element)myList2.get(j);

                String sName = signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_ID);
                CognitioTypeEnum typeEnum;
                UnitsEnum unit;
                boolean xSigned = false;

                if(sName == null){

                    return ReturnCode.XML_DATA_TAG_WITHOUT_NAME_ID;
                }

                String sTyp = signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_TYP);

                if(sTyp == null){
                    typeEnum = CognitioTypeEnum.TYP_BIT;

                }else{
                    typeEnum = CognitioTypeEnum.getEnum(sTyp);
                    if(typeEnum == null){

                        return ReturnCode.XML_DATA_TAG_TYPE_INVALID;
                    }
                }

                if(typeEnum == CognitioTypeEnum.TYP_BIT){
                    unit = UnitsEnum.UNIT_NONE;
                } else {
                    String sUnit = signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_UNIT);
                    unit = UnitsEnum.getEnum(sUnit);
                    if(unit == null) {
                        return ReturnCode.XML_DATA_TAG_UNIT_INVALID;
                    }

                    String sSigned = signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_SIGNED);
                    if(sSigned == null){
                        return ReturnCode.XML_DATA_TAG_TYPE_SIGNED_PROPERTY_NOT_VALID;
                    }

                    xSigned = (sSigned.toLowerCase().equals("true")) ? true : false;
                }



                String sKommentar = signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_COMMENT);
                if(sKommentar == null){
                    sKommentar = "";
                }

                String sUmrechnung = signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_UMRECHNUNG);
                if(sUmrechnung == null){
                    sUmrechnung = "";
                }

                boolean xAlert;
                String sAlert = signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_EREIGNIS);
                if(sAlert == null){
                    xAlert = false;
                }else{
                    xAlert = true;
                }

                Integer id;
                try {
                    id = Integer.parseInt(signalNode.getAttributeValue(UtilsConfiguration.XML_UMF_INDEX));
                } catch (NumberFormatException e){
                    LOG.error("index not found " + e);
                    id = j;
                }

                if(sInterface.equals(UtilsConfiguration.XML_UMF_EKE)){
                    DataTagEke umfelddatum = new DataTagEke(id, sName, sKommentar, unit, typeEnum, sUmrechnung, xSigned, xAlert);
                    cnf.getDataTagContainer().addUmfeldwert(umfelddatum);
                } else {
                    return ReturnCode.XML_INVALID_INTERFACE;
                }
            }
        }

        return ReturnCode.OK;
    }
 
    private static ReturnCode readKategorienXML(BaseConfigurationManager cnf, Element element)
    {
        Element kategorieNode, temp1, temp2;

        List myList = element.getChildren();
        for(int i = 0; i < myList.size(); i++){
            kategorieNode = (Element)myList.get(i);

            String sName = kategorieNode.getChildText(UtilsConfiguration.XML_UMFKAT_NAME);
            if(sName == null){

                return ReturnCode.XML_CATEGORY_WITHOUT_NAME;
            }

            Integer catId;
            try{
                catId = Integer.parseInt(kategorieNode.getChildText(UtilsConfiguration.XML_EREIG_KATID));
            } catch (NumberFormatException e){
                LOG.error(e);
                catId = i;
            }

            int iBuffer = -1;
            try{
                iBuffer = Integer.parseInt(kategorieNode.getChildText(UtilsConfiguration.XML_UMFKAT_BUFFERID));
                iBuffer = iBuffer -1;
                if((iBuffer < 0) || iBuffer >= UtilsConfiguration.MAXBUFFER){

                    return ReturnCode.XML_CATEGORY_WITH_WRONG_BUFFER_ID;
                }
            }catch(NumberFormatException ex){

                return ReturnCode.XML_CATEGORY_BUFFER_ID_NOT_A_NUMBER;
            }

            Category umfKategorie;
            try{

                DataBuffer tempBuffer = cnf.getDataBuffers().get(iBuffer);
                umfKategorie = new Category(catId, sName);
                tempBuffer.addKategorie(umfKategorie);
                umfKategorie.setBuffer(tempBuffer);
                cnf.getCategories().add(umfKategorie);

            }catch(ArrayIndexOutOfBoundsException exc){

                return ReturnCode.XML_CATEGORY_INDEX_OUT_OF_BOUNDS;
            }

            temp1 = kategorieNode.getChild(UtilsConfiguration.XML_UMFKAT_ANALOG);
            if(temp1 != null){
                List myListAnalog = temp1.getChildren();

                for(int j = 0; j < myListAnalog.size(); j++){
                    temp2 = (Element)myListAnalog.get(j);

                    String sSignalName = temp2.getText();
                    if(sSignalName != null){

                        DataTagValue tempUmfelddatum = cnf.getDataTagContainer().getUmfelddatum(sSignalName);
                        if(tempUmfelddatum != null){

                            umfKategorie.getMyAnalogSignals().add(tempUmfelddatum);
                        }else{

                            return ReturnCode.XML_CATEGORY_DATA_TAG_VALUE_NOT_FOUND;
                        }
                    }
                }
            }

            temp1 = kategorieNode.getChild(UtilsConfiguration.XML_UMFKAT_DIGITAL);
            if(temp1 != null){ // Digitale Signale
                List myListDigital = temp1.getChildren();

                for(int j = 0; j < myListDigital.size(); j++){
                    temp2 = (Element)myListDigital.get(j);

                    String sSignalName = temp2.getText();
                    if(sSignalName != null){

                        DataTagValue tempUmfelddatum = cnf.getDataTagContainer().getUmfelddatum(sSignalName);
                        if(tempUmfelddatum != null){

                            umfKategorie.getMyDigitalSignals().add(tempUmfelddatum);
                        }else{

                            return ReturnCode.XML_CATEGORY_DATA_TAG_VALUE_NOT_FOUND;
                        }
                    }
                }
            }
        }

        return ReturnCode.OK;
    }

    private static ReturnCode readEreignisXML(BaseConfigurationManager cnf, Element element)
    {
        Element ereignisNode, kategorienNode, temp1;

        List myList = element.getChildren();
        for(int i = 0; i < myList.size(); i++){
            ereignisNode = (Element)myList.get(i);

            String sName = ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_NAME);
            if(sName == null){

                return ReturnCode.XML_EVENT_WITHOUT_NAME;
            }

            String sReferenz = ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_REF);
            if(sReferenz == null){
                sReferenz = "";
            }

            String sUICKategorie = ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_UICKAT);
            if(sUICKategorie == null){

                return ReturnCode.XML_EVENT_WITHOUT_UIC_CATEGORY;
            }

            int iMaxAuftreten;
            String sMaxFehler = ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_MAX);
            if(sMaxFehler == null){

                return ReturnCode.XML_EVENT_WITHOUT_MAX_NUMBER_ENTRIES;
            }

            try{
                iMaxAuftreten = Integer.parseInt(sMaxFehler);
            }catch(NumberFormatException ex){

                return ReturnCode.XML_EVENT_MAX_NUMBER_ENTRIES_NOT_A_NUMBER;
            }

            boolean xReact;
            String sReact = ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_REACT);
            if(sReact == null){
                xReact = false;
            }else{
                xReact = true;
            }

            boolean iPre = false;
            String sPre = ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_PRE);
            if(sPre != null){

                iPre = true;
            }

            boolean iPost = false;
            String sPost = ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_POST);
            if(sPost != null){

                iPost = true;
            }

            Integer id;
            try{
                id = Integer.parseInt(ereignisNode.getAttributeValue(UtilsConfiguration.XML_EREIG_ID));
            } catch (NumberFormatException e){
                LOG.error("id not found " + e);
                id = i;
            }


            DataTagEvent neuesEreignis = new DataTagEvent(id, sName, sReferenz, UICCategoryEnum.valueOf((String)sUICKategorie), xReact, iPre, iPost, iMaxAuftreten);

            // Kategorien
            kategorienNode = ereignisNode.getChild(UtilsConfiguration.XML_EREIG_KAT);
            if(kategorienNode != null){
                List myKategorien = kategorienNode.getChildren();
                for(int j = 0; j < myKategorien.size(); j++){
                    temp1 = (Element)myKategorien.get(j);

                    Iterator<Category> itKategorien = cnf.getCategories().iterator();
                    while(itKategorien.hasNext()){
                        Category tempKategorie = itKategorien.next();
                        if(tempKategorie.getName().equals(temp1.getText())){
                            neuesEreignis.getKategorien().add(tempKategorie);
                            break;
                        }
                    }
                }
            }

            // Texte
            kategorienNode = ereignisNode.getChild(UtilsConfiguration.XML_EREIG_TEXTE);
            for(int j = 0; j < 3; j++){
                String sChildKurztext = "sp" + j + "_kt";
                String sKurztext = kategorienNode.getChildText(sChildKurztext);
                if(sKurztext == null){
                    neuesEreignis.getKurztext()[j] ="";
                }else{
                    neuesEreignis.getKurztext()[j] =sKurztext;
                }

                for(int k = 0; k < 3; k++){
                    String sChildTexte = "sp" + j + "_t" + k;
                    String sChildText = kategorienNode.getChildText(sChildTexte);
                    if(sChildText != null){
                       neuesEreignis.getText()[j][k] = sChildText;
                    }
                }
            }


            Iterator it = cnf.getDataTagContainer().getEKEVector().iterator();
            while(it.hasNext()){
                DataTagValue tempUmfelddatum = (DataTagValue)it.next();
                if(tempUmfelddatum.getName().equals(sName)){
                    tempUmfelddatum.setEvent(neuesEreignis);
                    break;
                }
            }
        }
        
        return ReturnCode.OK;
    }
 
    public static boolean saveProjectPropertiesToXML(BaseConfigurationManager cnf, File konfigFile)
    {
        Element temp1, temp2, temp3, temp4;

        try{
            Document doc = new Document();

            Element root = new Element(UtilsConfiguration.XML_ROOT);
            doc.setRootElement(root);

           
            temp1 = new Element(UtilsConfiguration.XML_DISP);

            for(int i = 0; i < 3 ; i++){
                if(!cnf.getSprache()[i].equals("")){
                    temp2 = new Element(UtilsConfiguration.XML_DISP_LAN[i]).setText(cnf.getSprache()[i]);
                    temp1.addContent(temp2);
                }
            }

            if(cnf.isWerkstattText()){
                temp2 = new Element(UtilsConfiguration.XML_DISP_INFO).setText("true");
                temp1.addContent(temp2);
            }

            root.addContent(temp1);

            // Properties
            temp1 = new Element(UtilsConfiguration.XML_PROP);
            temp2 = new Element(UtilsConfiguration.XML_PROP_VER).setText(cnf.getVersion());
            temp1.addContent(temp2);

            temp2 = new Element(UtilsConfiguration.XML_PROP_PROJEKT).setText(cnf.getProjekt());
            temp1.addContent(temp2);

            temp2 = new Element(UtilsConfiguration.XML_PROP_PROJECT_HASH).setText(cnf.getProjekthash());
            temp1.addContent(temp2);

            temp2 = new Element(UtilsConfiguration.XML_PROP_DATA_ENDIANESS).setText(cnf.getByteOrder().name());
            temp1.addContent(temp2);

            temp2 = new Element(UtilsConfiguration.XML_PROP_HARDWARE).setText(cnf.getHardware().getValue());
            temp1.addContent(temp2);

            temp2 = new Element(UtilsConfiguration.XML_PROP_SERVER).setText("" + cnf.getServerPort());
            temp1.addContent(temp2);

            root.addContent(temp1);

            // Errorlog
            temp1 = new Element(UtilsConfiguration.XML_LOG);
            temp1.setAttribute(UtilsConfiguration.XML_LOG_SIZE, "" + cnf.getFehlerSize());
            if(cnf.isRingbuffer()){
                temp1.setAttribute(UtilsConfiguration.XML_LOG_RING, "true");
            }else{
                temp1.setAttribute(UtilsConfiguration.XML_LOG_RING, "false");
            }
            temp1.setAttribute(UtilsConfiguration.XML_LOG_INTERVAL, "" + cnf.getFehlerAbtast());
            root.addContent(temp1);

            // Fahrzeuge
            temp1 = new Element(UtilsConfiguration.XML_VEHICLE_ROOT);
            Iterator it = cnf.getFahrzeugList().iterator();
            while(it.hasNext()){
                 Fahrzeug temp = (Fahrzeug)it.next();
                temp2 = new Element(UtilsConfiguration.XML_VEHICLE);
                temp2.setAttribute(UtilsConfiguration.XML_VEHICLE_MD5, temp.getMD5Hash());
                temp3 = new Element(UtilsConfiguration.XML_VEHICLE_NAME).setText(temp.getFahrzeugName());
                temp2.addContent(temp3);
                temp3 = new Element(UtilsConfiguration.XML_VEHICLE_POS).setText(temp.getEinbauort());
                temp2.addContent(temp3);

                temp1.addContent(temp2);
            }

            root.addContent(temp1);

            // Buffer
            temp1 = new Element(UtilsConfiguration.XML_BUFFER_ROOT);
            it = cnf.getDataBuffers().iterator();

            while(it.hasNext()){
                DataBuffer temp = (DataBuffer)it.next();
                temp2 = new Element(UtilsConfiguration.XML_BUFFER);
                temp2.setAttribute(UtilsConfiguration.XML_BUFFER_ID, "" + temp.getBufferID());
                temp2.setAttribute(UtilsConfiguration.XML_BUFFER_SIZE, "" + temp.getSize());
                temp2.setAttribute(UtilsConfiguration.XML_BUFFER_S, "" + temp.getIntervall_s());
                temp2.setAttribute(UtilsConfiguration.XML_BUFFER_MS, "" + temp.getIntervall_ms());

                temp1.addContent(temp2);

            }

            root.addContent(temp1);

            // Umfelddaten EKE
            temp1 = new Element(UtilsConfiguration.XML_UMF_ROOT);

            writeSignalsToXml(cnf, temp1);
            root.addContent(temp1);

            temp1 = new Element(UtilsConfiguration.XML_UMFKAT_ROOT);


            for(Category cat : cnf.getCategories()){
                temp2 = new Element(UtilsConfiguration.XML_UMFKAT_KAT);
                temp3 = new Element(UtilsConfiguration.XML_UMFKAT_NAME).setText(cat.getName());
                temp2.addContent(temp3);

                temp3 = new Element(UtilsConfiguration.XML_EREIG_KATID).setText(cat.getId().toString());
                temp2.addContent(temp3);

                for(DataBuffer b : cnf.getDataBuffers()){
                    temp3 = new Element(UtilsConfiguration.XML_UMFKAT_BUFFERID).setText("" + b.getBufferID());
                    temp2.addContent(temp3);
                }

                temp3 = new Element(UtilsConfiguration.XML_UMFKAT_ANALOG);
                for(DataTagValue t : cat.getMyAnalogSignals()){
                    temp4 = new Element(UtilsConfiguration.XML_UMFKAT_SIGNAL).setText(t.getName());
                    temp3.addContent(temp4);

                }
                temp2.addContent(temp3);

                temp3 = new Element(UtilsConfiguration.XML_UMFKAT_DIGITAL);
                for(DataTagValue t : cat.getMyDigitalSignals()){
                    temp4 = new Element(UtilsConfiguration.XML_UMFKAT_SIGNAL).setText(t.getName());
                    temp3.addContent(temp4);
                }
                temp2.addContent(temp3);
                temp1.addContent(temp2);
            }

            root.addContent(temp1);


            temp1 = new Element(UtilsConfiguration.XML_EREIG_ROOT);

            for(DataTagValue t : cnf.getDataTagContainer().getEKEVector()){
                DataTagEvent tempEreignis = t.getEvent();
                if(tempEreignis != null){
                    temp1.addContent(createEreignisNode(t, tempEreignis));

                }
            }

            root.addContent(temp1);

            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            FileOutputStream writer = new FileOutputStream(konfigFile);
            out.output(doc, writer);
            writer.flush();
            writer.close();
        }catch (IOException e) {
            LOG.error(e);
            return false;
        }

        return true;
    }

    private static void writeSignalsToXml(BaseConfigurationManager cnf, Element parent) {

        if(cnf.getDataTagContainer().getEKEVector().size() > 0) {
            Element tagClassName = new Element(UtilsConfiguration.XML_UMF_EKE);
            parent.addContent(tagClassName);

            for (DataTagEke tag : cnf.getDataTagContainer().getEKEVector()) {

                Element signalElement = new Element(UtilsConfiguration.XML_UMF_SIGNAL);
                signalElement.setAttribute(UtilsConfiguration.XML_UMF_ID, tag.getName());
                signalElement.setAttribute(UtilsConfiguration.XML_UMF_INDEX, tag.getId().toString());

                if (!tag.getCommentar().equals("")) {
                    signalElement.setAttribute(UtilsConfiguration.XML_UMF_COMMENT, tag.getCommentar());
                }

                if (tag.isEreignis()) {
                    signalElement.setAttribute(UtilsConfiguration.XML_UMF_EREIGNIS, "1");
                } else {
                    signalElement.setAttribute(UtilsConfiguration.XML_UMF_EREIGNIS, "0");
                }

                if (!tag.getType().equals(CognitioTypeEnum.TYP_BIT)) {
                    signalElement.setAttribute(UtilsConfiguration.XML_UMF_TYP, tag.getType().getValue());
                    signalElement.setAttribute(UtilsConfiguration.XML_UMF_SIGNED, "" + tag.isSigned());

                    if (!tag.getUmrechnung().equals("")) {
                        signalElement.setAttribute(UtilsConfiguration.XML_UMF_UMRECHNUNG, tag.getUmrechnung());
                    }

                    signalElement.setAttribute(UtilsConfiguration.XML_UMF_UNIT, tag.getUnit().getValue());
                }

                tagClassName.addContent(signalElement);
            }
        }
    }


    private static Element createEreignisNode(DataTagValue tempUmfelddatum, DataTagEvent tempEreignis)
    {
        Element temp2 = new Element(UtilsConfiguration.XML_EREIG);
        Element temp3, temp4;

        if(tempUmfelddatum.isEreignis()){
            temp2.setAttribute(UtilsConfiguration.XML_EREIG_ID, "" + tempEreignis.getId());
        }

        temp2.setAttribute(UtilsConfiguration.XML_EREIG_NAME, tempEreignis.getName());

        if(tempEreignis.isReact()){
            temp2.setAttribute(UtilsConfiguration.XML_EREIG_REACT, "true");
        }

        if(!tempEreignis.getReferenz().equals("")){
            temp2.setAttribute(UtilsConfiguration.XML_EREIG_REF, tempEreignis.getReferenz());
        }

        UICCategoryEnum uic = tempEreignis.getuICKategorie();

        temp2.setAttribute(UtilsConfiguration.XML_EREIG_UICKAT, uic.name());

        if(tempEreignis.isPreHistory()){
            temp2.setAttribute(UtilsConfiguration.XML_EREIG_PRE, "true");
        }

        if(tempEreignis.isPostHistory()){
            temp2.setAttribute(UtilsConfiguration.XML_EREIG_POST, "true");
        }

        temp2.setAttribute(UtilsConfiguration.XML_EREIG_MAX, "" + tempEreignis.getMaxAuftreten());

        temp3 = new Element(UtilsConfiguration.XML_EREIG_TEXTE);
        for(int j=0; j<3; j++){
            String sKurztext = tempEreignis.getKurztext()[j];
            if(sKurztext != null && (!sKurztext.equals(""))){
                temp4 = new Element("sp" + j + "_kt");
                temp4.setText(sKurztext);
                temp3.addContent(temp4);
            }

            for(int k=0; k<3; k++){
                String sText = tempEreignis.getText()[j][k];
                if((sText != null) && (!sText.equals(""))){
                    temp4 = new Element("sp" + j + "_t" + k);
                    temp4.setText(sText);
                    temp3.addContent(temp4);
                }
            }
        }

        temp2.addContent(temp3);

        if(tempEreignis.getKategorien().size() > 0){
            temp3 = new Element(UtilsConfiguration.XML_EREIG_KAT);
            Iterator itKategorie = tempEreignis.getKategorien().iterator();
            while(itKategorie.hasNext()){
                Category tempKat = (Category)itKategorie.next();
                temp4 = new Element(UtilsConfiguration.XML_EREIG_KATID);
                temp4.setText(tempKat.getName());
                temp3.addContent(temp4);
            }
            temp2.addContent(temp3);
        }

        return temp2;
    }

    private static ReturnCode readEnvDataODS(BaseConfigurationManager cnf, Sheet sheet, int rowNumber, String sName, String sKommentar, CognitioTypeEnum sTyp)
    {
        Object oSigned = sheet.getValueAt(3,rowNumber);
        Object oUmrechnung = sheet.getValueAt(4,rowNumber);
        Object oEinheit = sheet.getValueAt(5,rowNumber);

        if(oSigned instanceof String && oUmrechnung instanceof String && oEinheit instanceof String){
            boolean xSigned;
            if(((String)oSigned).toLowerCase().equals("signed")){
                xSigned = true;
            }else if(((String)oSigned).toLowerCase().equals("unsigned")){
                xSigned = false;
            }else{

                return ReturnCode.ODS_SIGNED_FIELD_NOT_VALID;
            }

            boolean xVorhanden = (UnitsEnum.getEnum((String)oEinheit) != null) ? true : false;


            if(!xVorhanden){

                return ReturnCode.ODS_UNITS_NOT_FOUND;
            }

            DataTagEke tempEKE = new DataTagEke(rowNumber - 7, sName, sKommentar,  UnitsEnum.getEnum((String)oEinheit), sTyp, (String)oUmrechnung, xSigned, false);
            //cnf.getDataTagContainer().addUmfeldwert(tempEKE);
            cnf.getDataTagContainer().getEKEVector().add(tempEKE);
            // Kategorienzuweisung
            for(int j = 0; j < cnf.getCategories().size(); j++){
                Object oKat = sheet.getValueAt(j + 6, rowNumber);
                if(oKat instanceof String){
                    if(((String)oKat).toLowerCase().equals("x")){
                        cnf.getCategories().get(j).getMyAnalogSignals().add(tempEKE);
                    }
                }
            }
        }

        return ReturnCode.OK;
    }

     
    public static boolean saveProjectPropertiesToODS(Class clazz, BaseConfigurationManager cnf, File saveFile)
    {
        //URL template = ClassLoader.getSystemResource("config/template_config.ods");
        String res = "config/template_config.ods";

        URL template = clazz.getClassLoader().getResource(res);

        if(template == null){
            LOG.error("template file not found " + res);
        }

        // Vorhandene Datei wird gelöscht !!!
        String sFile = saveFile.getAbsolutePath();
        if(saveFile.exists()){
            saveFile.delete();
        }


        File fDest = new File(sFile);
        if(copyFile(template, fDest)){
            try{
                final SpreadSheet spread = SpreadSheet.createFromFile(fDest);

                Sheet sheetTitle = spread.getSheet(UtilsConfiguration.ODS_TITLE);
                if(sheetTitle == null){
                    return false;
                }

                Sheet sheetFahrzeuge = spread.getSheet(UtilsConfiguration.ODS_FAHRZEUGE);
                if(sheetFahrzeuge == null){
                    return false;
                }

                Sheet sheetBuffer = spread.getSheet(UtilsConfiguration.ODS_BUFFER);
                if(sheetBuffer == null){
                    return false;
                }

                Sheet sheetEKE = spread.getSheet(UtilsConfiguration.ODS_UMFELD);
                if(sheetEKE == null){
                    return false;
                }

                Sheet sheetEreignisse = spread.getSheet(UtilsConfiguration.ODS_EREIGNISSE);
                if(sheetEreignisse == null){
                    return false;
                }

                for(int i = 0; i < 3 ; i++){
                    // Titelseite
                    sheetTitle.setValueAt(cnf.getSprache()[i], 2, 19 + i);

                }

                if(cnf.isWerkstattText()){
                    sheetTitle.setValueAt("true", 2, 23);
                }else{
                    sheetTitle.setValueAt("false", 2, 23);
                }

                sheetTitle.setValueAt(cnf.getVersion(), 2, 26);
                sheetTitle.setValueAt(cnf.getHardware().getValue(), 2, 27);
                sheetTitle.setValueAt(cnf.getProjekt(), 2, 28);

                sheetTitle.setValueAt("" + cnf.getFehlerSize(), 2, 31);
                sheetTitle.setValueAt("" + cnf.getFehlerAbtast(), 2, 32);
                if(cnf.isRingbuffer()){
                    sheetTitle.setValueAt("true", 2, 33);
                }else{
                    sheetTitle.setValueAt("false", 2, 33);
                }
                sheetTitle.setValueAt("" + cnf.getServerPort(), 2, 35);

                // Fahrzeuge
                int iRowCount = sheetFahrzeuge.getRowCount();
                if(iRowCount < cnf.getFahrzeugList().size() + 10){
                    sheetFahrzeuge.setRowCount(cnf.getFahrzeugList().size() + 10);
                }

                int i=6;
                Iterator it = cnf.getFahrzeugList().iterator();
                while(it.hasNext()){
                    Fahrzeug tempFahrzeug = (Fahrzeug)it.next();
                    sheetFahrzeuge.setValueAt(tempFahrzeug.getFahrzeugName(), 0, i);
                    sheetFahrzeuge.setValueAt(tempFahrzeug.getEinbauort(), 1, i);
                    sheetFahrzeuge.setValueAt(tempFahrzeug.getMD5Hash(), 2, i);
                    i++;
                }

                // Buffer
                i=6;
                it = cnf.getDataBuffers().iterator();
                while(it.hasNext()){
                    DataBuffer tempBuffer = (DataBuffer)it.next();

                    sheetBuffer.setValueAt((tempBuffer.getIntervall_s() + (tempBuffer.getIntervall_ms()/1000f)), 1, i);
                    sheetBuffer.setValueAt(tempBuffer.getSize(), 2, i);
                    i++;
                }

                // Kategorien
                Sheet sheetKategorien = spread.getSheet(UtilsConfiguration.ODS_KATEGORIEN);

                i=6;
                it = cnf.getCategories().iterator();
                while(it.hasNext()){
                    Category tempKategorie = (Category)it.next();

                    sheetKategorien.setValueAt(tempKategorie.getName(), 1, i);
                    sheetKategorien.setValueAt(tempKategorie.getBuffer().getBufferID(), 2, i);
                    i++;
                }


                // EKE Attribute
                iRowCount = sheetEKE.getRowCount();
                if(iRowCount <  cnf.getDataTagContainer().getEKEVector().size() + 10){
                    sheetEKE.setRowCount(cnf.getDataTagContainer().getEKEVector().size() + 10);
                }

                int j=7;
                i=7;

                sheetEKE.setRowCount(cnf.getDataTagContainer().getEKEVector().size() + 10);
                sheetEreignisse.setRowCount(cnf.getDataTagContainer().getEKEVector().size() + 10);

                it = cnf.getDataTagContainer().getEKEVector().iterator();
                while(it.hasNext()){
                    DataTagValue tempUmfelddatum = (DataTagValue)it.next();

                    sheetEKE.setValueAt(tempUmfelddatum.getName(), 0, i);
                    sheetEKE.setValueAt(tempUmfelddatum.getCommentar(), 1, i);
                    sheetEKE.setValueAt(tempUmfelddatum.getType().getValue(), 2, i);


                    if(tempUmfelddatum.isSigned()){
                        sheetEKE.setValueAt("signed", 3, i);
                    }else{
                        sheetEKE.setValueAt("unsigned", 3, i);
                    }

                    sheetEKE.setValueAt(tempUmfelddatum.getUmrechnung(), 4, i);
                    sheetEKE.setValueAt(tempUmfelddatum.getUnit(), 5, i);

                    int iKategorie = 0;
                    Iterator itKategorien = cnf.getCategories().iterator();
                    while(itKategorien.hasNext()){
                        Category tempKategorie = (Category)itKategorien.next();

                        if(tempKategorie.contains(tempUmfelddatum)){
                            sheetEKE.setValueAt("x", 6 + iKategorie, i);
                        }

                        iKategorie++;
                    }

                    i++;

                    if(tempUmfelddatum.isEreignis()){
                        DataTagEvent tempEreignis = tempUmfelddatum.getEvent();

                        sheetEreignisse.setValueAt(tempEreignis.getReferenz(), 0, j);
                        sheetEreignisse.setValueAt(tempEreignis.getName(), 1, j);
                        sheetEreignisse.setValueAt(tempEreignis.getuICKategorie(), 2, j);

                        if(tempEreignis.isReact()){
                            sheetEreignisse.setValueAt("x", 3, j);
                        }

                        if(tempEreignis.isPreHistory()){
                            sheetEreignisse.setValueAt("x", 4, j);
                        }

                        if(tempEreignis.isPostHistory()){
                            sheetEreignisse.setValueAt("x", 5, j);
                        }

                        sheetEreignisse.setValueAt(tempEreignis.getMaxAuftreten(), 6, j);

                        sheetEreignisse.setValueAt(tempEreignis.getKurztext()[0], 7, j);
                        sheetEreignisse.setValueAt(tempEreignis.getKurztext()[1], 11, j);
                        sheetEreignisse.setValueAt(tempEreignis.getKurztext()[2], 15, j);

                        int iPos = 8;
                        for(int k=0; k<3; k++){
                            for(int l=0; l<3; l++){
                                sheetEreignisse.setValueAt(tempEreignis.getText()[k][l], iPos, j);
                                iPos++;
                            }
                            iPos++;
                        }

                        itKategorien = tempEreignis.getKategorien().iterator();
                        while(itKategorien.hasNext()){
                            Category tempKategorie = (Category)itKategorien.next();

                            int iPosKategorie = cnf.getCategories().indexOf(tempKategorie);
                            if(iPosKategorie >= 0){
                                sheetEreignisse.setValueAt("x", 19 + iPosKategorie ,j);
                            }
                        }

                        j++;
                    }
                }

                // Datei speichern
                spread.saveAs(fDest);
            }catch(IOException exc){
                return false;
            }
        }else{
            return false;
        }

        return true;
    }

    /**
     *
     * @param diagnoseFile
     * @return
     */
    public static boolean saveDiagnosticsConfigurationToDiagd(BaseConfigurationManager cnf, File diagnoseFile)
    {
        Element temp1, temp2;
        int[] iStart = calcStartOfKategorien(cnf);

        try{
            Document doc = new Document();

            Element root = new Element(UtilsConfiguration.DID_ROOT);
            doc.setRootElement(root);

            temp1 = new Element(UtilsConfiguration.DID_PROJEKT);
            temp1.setAttribute(UtilsConfiguration.DID_PROJEKT_ID, cnf.getProjekthash());
            root.addContent(temp1);

            temp1 = new Element(UtilsConfiguration.DID_SERVER);
            temp1.setAttribute(UtilsConfiguration.DID_SERVER_PORT, "" + cnf.getServerPort());
            root.addContent(temp1);

            temp1 = new Element(UtilsConfiguration.DID_ERRLOG);
            temp1.setAttribute(UtilsConfiguration.DID_ERRLOG_SIZE, "" + cnf.getFehlerSize());
            if(cnf.isRingbuffer()){
                temp1.setAttribute(UtilsConfiguration.DID_ERRLOG_RINGBUF, "true");
            }else{
                temp1.setAttribute(UtilsConfiguration.DID_ERRLOG_RINGBUF, "false");
            }
            temp1.setAttribute(UtilsConfiguration.DID_INTERVAL, "" + cnf.getFehlerAbtast());
            root.addContent(temp1);

            temp1 = new Element(UtilsConfiguration.DID_BUFFER_ROOT);
            Iterator it = cnf.getDataBuffers().iterator();
            int i = 0;
            while(it.hasNext()){
                DataBuffer tempBuffer = (DataBuffer)it.next();
                temp2 = new Element(UtilsConfiguration.DID_BUFFER);
                temp2.setAttribute(UtilsConfiguration.DID_BUFFER_ID, "" + i);
                temp2.setAttribute(UtilsConfiguration.DID_BUFFER_S, "" + tempBuffer.getIntervall_s());
                temp2.setAttribute(UtilsConfiguration.DID_BUFFER_MS, "" + tempBuffer.getIntervall_ms());
                temp2.setAttribute(UtilsConfiguration.DID_BUFFER_SIZE, "" + tempBuffer.getSize());
                temp1.addContent(temp2);
                i++;
            }
            root.addContent(temp1);

            temp1 = new Element(UtilsConfiguration.DID_UMFKAT_ROOT);
            temp1.setAttribute(UtilsConfiguration.DID_UMFKAT_ANZ, "" + cnf.getCategories().size());
            it = cnf.getCategories().iterator();
            i = 0;
            while(it.hasNext())
            {
                Category tempKategorie = (Category)it.next();
                temp2 = new Element(UtilsConfiguration.DID_UMFKAT);
                temp2.setAttribute(UtilsConfiguration.DID_UMFKAT_ID, "" + i);
                if(tempKategorie.getBuffer() == null){
                    LOG.error("category as no buffer");
                    return false;
                }

                temp2.setAttribute(UtilsConfiguration.DID_UMFKAT_BUFFERID, "" + tempKategorie.getBuffer().getBufferID());
                temp2.setAttribute(UtilsConfiguration.DID_UMFKAT_START, "" + iStart[i]);
                temp2.setAttribute(UtilsConfiguration.DID_UMFKAT_SIZE, "" + tempKategorie.getSizeInBytes());

                createSignalElements(temp2, tempKategorie.getMyAnalogSignals());
                createSignalElements(temp2, tempKategorie.getMyDigitalSignals());


                temp1.addContent(temp2);
                i++;
            }
            root.addContent(temp1);

            temp1 = new Element(UtilsConfiguration.DID_EREIG_ROOT);

           int iAnzMaxInsert = 0;

            it = cnf.getDataTagContainer().getEKEVector().iterator();
            while(it.hasNext()){
                DataTagValue tempUmfelddatum = (DataTagValue)it.next();
                if(tempUmfelddatum.isEreignis()){
                    DataTagEvent tempEreignis = tempUmfelddatum.getEvent();

                    if(tempEreignis == null){
                        LOG.error(String.format("the event is null for %s", tempUmfelddatum.getName()));
                        continue;
                    }

                    temp1.addContent(createEreignisNode(cnf, tempEreignis));
                    if(tempEreignis.getMaxAuftreten() != 0){
                        iAnzMaxInsert++;
                    }
                }
            }

            temp1.setAttribute("anz", "" + cnf.getDataTagContainer().getEKEVector().size());
            temp1.setAttribute("maxcount", "" + iAnzMaxInsert);
            root.addContent(temp1);

            XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            FileOutputStream writer = new FileOutputStream(diagnoseFile);
            out.output(doc, writer);
            writer.flush();
            writer.close();
        }catch (IOException e) {

            return false;
        }

        return true;
    }

    private static void createSignalElements(Element parent, Set<DataTagValue> mySignals) {


        for(DataTagValue v : mySignals){
            Element e = new Element("signal");
            if(v.getEvent() != null){
                e.setAttribute(UtilsConfiguration.EVENT_INDEX, v.getId().toString());
            }

            e.setAttribute("name", v.getName());
            e.setAttribute("type", v.getType().getValue());
            e.setAttribute("size", v.getType().getSize().toString());
            parent.addContent(e);
        }



    }

    /**
     *
     * @param tempEreignis
     * @return
     */
    private static Element createEreignisNode(BaseConfigurationManager cnf, DataTagEvent tempEreignis)
    {
        Element temp2 = new Element(UtilsConfiguration.DID_EREIG);
        Element temp3, temp4;

        // Ereignisindex
        temp2.setAttribute(UtilsConfiguration.EVENT_INDEX, "" +  tempEreignis.getId().toString());

        // Attributkombination
        byte bInit = 0x00;
        if(tempEreignis.isReact()){
            bInit |= 0x01;
        }

        if(tempEreignis.isPreHistory()){
            bInit |= 0x02;
        }

        if(tempEreignis.isPostHistory()){
            bInit |= 0x04;
        }

        int iKat = tempEreignis.getuICKategorie().ordinal();
        bInit |= (iKat << 3);

        temp2.setAttribute("init", "" + bInit);

        // Anzahl Einträge im Datenlogger
        temp2.setAttribute("count", "" + tempEreignis.getMaxAuftreten());

        // Gruppen
        int iAnzGruppen = tempEreignis.getKategorien().size();
        if(iAnzGruppen > 0){
            temp3 = new Element("kategorien");
            temp3.setAttribute("anz", "" + iAnzGruppen);

            Iterator itKategorien = tempEreignis.getKategorien().iterator();
            while(itKategorien.hasNext()){
                Category tempKategorie = (Category)itKategorien.next();

                Iterator itBuffer = cnf.getDataBuffers().iterator();
                while(itBuffer.hasNext()){
                    DataBuffer tempBuffer = (DataBuffer)itBuffer.next();
                    if(tempBuffer.containsKategorie(tempKategorie)){
                        temp4 = new Element("kategorie");
                        //int iKategorienIndex = cnf.getCategories().indexOf(tempKategorie);
                        //temp4.setAttribute("kid", "" + iKategorienIndex);
                        temp4.setAttribute("kid", "" + tempKategorie.getId());
                        temp4.setAttribute("bid", "" + tempBuffer.getBufferID());

                        temp3.addContent(temp4);
                    }
                }
            }

            temp2.addContent(temp3);
        }

        return temp2;
    }

    /**
     *
     */
    public static boolean speichereEKEKonfiguration(BaseConfigurationManager cnf, File saveFile)
    {
        int iTempBytes = 0;
        int iAnzReal = 0;

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(saveFile);

            fos.write("(* Railtec Systems GmbH                           *)\n".getBytes());
            fos.write("(* Sonnenbergstrasse 19                           *)\n".getBytes());
            fos.write("(* CH-6052 Hergiswil                              *)\n\n".getBytes());
            fos.write("(* Author: Automatic generated by Cognitio        *)\n".getBytes());
            fos.write(("(* ProjektID: " + cnf.getVersion() + "    *)\n").getBytes());
            fos.write(("(* ProjektID: " + cnf.getProjekthash() + "    *)\n").getBytes());

            fos.write(("\n(* Ereignisse *)\n").getBytes());

            Iterator itFehler = cnf.getDataTagContainer().getEKEVector().iterator();
            int iAnzFehler = 0;
            while(itFehler.hasNext()){
                DataTagValue tempUmfelddatum = (DataTagValue)itFehler.next();
                if(tempUmfelddatum.isEreignis()){
                    fos.write(("EREIGNISSE.tBuffer[" + iAnzFehler + "] := ANY_TO_BYTE(" + tempUmfelddatum.getName() + ");\n").getBytes());
                    iAnzFehler++;
                }
            }

            fos.write(("\n\n(* Prozesswerte *)\n").getBytes());
            Iterator itBuffer = cnf.getDataBuffers().iterator();
            while(itBuffer.hasNext()){
                DataBuffer tempBuffer = (DataBuffer)itBuffer.next();

                int iStart = 0;
                Iterator itKategorie = tempBuffer.getKategorien().values().iterator();
                while(itKategorie.hasNext()){
                    Category tempKategorie = (Category)itKategorie.next();

                    int iBuffer = tempBuffer.getBufferID();
                    fos.write(("\n\n(* Kategory " + tempKategorie.getName() + " - Buffer = " + iBuffer + " *)\n").getBytes());

                    Iterator itAnalog = tempKategorie.getMyAnalogSignals().iterator();
                    while(itAnalog.hasNext()){
                        DataTagValue tempUmfeldatum = (DataTagValue)itAnalog.next();

                        if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_BYTE)){ // Byte (1 Byte)
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := ANY_TO_BYTE(" + tempUmfeldatum.getName() + ");\n").getBytes());
                            iStart += 1;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_WORD)){ // Word (2 Byte)
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := ANY_TO_BYTE(ROR(ANY_TO_DINT(" + tempUmfeldatum.getName() + "),8));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 1) + "] := ANY_TO_BYTE(" + tempUmfeldatum.getName() + ");\n").getBytes());
                            iStart += 2;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_DWORD)){ // DWord (4 Byte)
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := ANY_TO_BYTE(ROR(ANY_TO_DINT(" + tempUmfeldatum.getName() + "),24));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 1) + "] := ANY_TO_BYTE(ROR(ANY_TO_DINT(" + tempUmfeldatum.getName() + "),16));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 2) + "] := ANY_TO_BYTE(ROR(ANY_TO_DINT(" + tempUmfeldatum.getName() + "),8));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 3) + "] := ANY_TO_BYTE(" + tempUmfeldatum.getName() + ");\n").getBytes());
                            iStart += 4;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_INT)){ // INT
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := ANY_TO_BYTE(ROR(ANY_TO_DINT(" + tempUmfeldatum.getName() + "),8));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 1) + "] := ANY_TO_BYTE(" + tempUmfeldatum.getName() + ");\n").getBytes());
                            iStart += 2;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_DINT)){ // DINT
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := ANY_TO_BYTE(ROR(" + tempUmfeldatum.getName() + ",24));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 1) + "] := ANY_TO_BYTE(ROR(" + tempUmfeldatum.getName() + ",16));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 2) + "] := ANY_TO_BYTE(ROR(" + tempUmfeldatum.getName() + ",8));\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 3) + "] := ANY_TO_BYTE(" + tempUmfeldatum.getName() + ");\n").getBytes());
                            iStart += 4;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_REAL)){ // REAL

                            fos.write(("\nconv_real(" + tempUmfeldatum.getName() + ");\n").getBytes());
                            fos.write(("conv_real_" + iAnzReal + " := conv_real.real_byte_array;\n").getBytes());

                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := ANY_TO_BYTE(conv_real_" + iAnzReal + ".b1);\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 1) + "] := ANY_TO_BYTE(conv_real_" + iAnzReal + ".b2);\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 2) + "] := ANY_TO_BYTE(conv_real_" + iAnzReal + ".b3);\n").getBytes());
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + (iStart + 3) + "] := ANY_TO_BYTE(conv_real_" + iAnzReal + ".b4);\n").getBytes());
                            iStart += 4;
                            iAnzReal++;
                        }
                    }

                    int iDigital = 0;
                    fos.write(("\n").getBytes());
                    Iterator itDigital = tempKategorie.getMyDigitalSignals().iterator();
                    while(itDigital.hasNext()){
                        DataTagValue tempUmfelddatum = (DataTagValue)itDigital.next();
                        fos.write(("tempByte" + iTempBytes + "." + iDigital + " := " + tempUmfelddatum.getName() + ";\n").getBytes());
                        iDigital++;

                        if(iDigital == 7){
                            fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := tempByte" + iTempBytes + ";\n").getBytes());
                            iStart++;
                            iDigital = 0;
                            iTempBytes++;
                        }
                    }

                    if(iDigital != 0){
                        fos.write(("BUFFER_" + iBuffer + ".tBuffer[" + iStart + "] := tempByte" + iTempBytes + ";\n").getBytes());
                        iTempBytes++;
                    }
                }
            }

        }catch(IOException ex){
            return false;
        }finally{
            if(fos != null){
                try {
                    fos.close();
                }catch(IOException ex){
                    return false;
                }
            }
        }

        return true;
    }


    public static boolean speichereCCDispKonfiguration(BaseConfigurationManager cnf, File savefile)
    {
        Element temp1, temp2;

        int iAnzReal = 0;

        try{
            Document doc = new Document();

            Element root = new Element(UtilsConfiguration.CC_ROOT);
            doc.setRootElement(root);

            // Umfelddaten
            temp1 = new Element(UtilsConfiguration.CC_DATA);

            Iterator itFehler = cnf.getDataTagContainer().getEKEVector().iterator();
            int iAnzFehler = 0;
            while(itFehler.hasNext()){
                DataTagValue tempUmfelddatum = (DataTagValue)itFehler.next();
                if(tempUmfelddatum.isEreignis()){
                    temp2 = new Element(UtilsConfiguration.CC_ITEM);
                    temp2.setAttribute(UtilsConfiguration.CC_ITEM_NAME, tempUmfelddatum.getName());
                    temp2.setAttribute(UtilsConfiguration.CC_ITEM_OFFSET, "" + iAnzFehler);

                    temp1.addContent(temp2);
                    iAnzFehler++;
                }
            }

            // Prozesswerte
            Iterator itBuffer = cnf.getDataBuffers().iterator();
            while(itBuffer.hasNext()){
                DataBuffer tempBuffer = (DataBuffer)itBuffer.next();

                int iStart = 0;
                Iterator itKategorie = tempBuffer.getKategorien().values().iterator();
                while(itKategorie.hasNext()){
                    Category tempKategorie = (Category)itKategorie.next();

                    int iBuffer = tempBuffer.getBufferID();
                    int iBufferOffset = (iBuffer * 1024) + 1024;

                    Iterator itAnalog = tempKategorie.getMyAnalogSignals().iterator();
                    while(itAnalog.hasNext()){
                        DataTagValue tempUmfeldatum = (DataTagValue)itAnalog.next();

                        if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_BYTE)){ // Byte (1 Byte)
                            temp2 = new Element(UtilsConfiguration.CC_ITEM);
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_NAME, tempUmfeldatum.getName());
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_OFFSET, "" + (iBufferOffset + iStart));

                            temp1.addContent(temp2);

                            iStart += 1;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_WORD)){ // Word (2 Byte)
                            temp2 = new Element(UtilsConfiguration.CC_ITEM);
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_NAME, tempUmfeldatum.getName());
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_OFFSET, "" + (iBufferOffset + iStart));

                            iStart += 2;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_DWORD)){ // DWord (4 Byte)
                            temp2 = new Element(UtilsConfiguration.CC_ITEM);
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_NAME, tempUmfeldatum.getName());
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_OFFSET, "" + (iBufferOffset + iStart));

                            iStart += 4;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_INT)){ // INT
                            temp2 = new Element(UtilsConfiguration.CC_ITEM);
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_NAME, tempUmfeldatum.getName());
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_OFFSET, "" + (iBufferOffset + iStart));

                            iStart += 2;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_DINT)){ // DINT
                            temp2 = new Element(UtilsConfiguration.CC_ITEM);
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_NAME, tempUmfeldatum.getName());
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_OFFSET, "" + (iBufferOffset + iStart));

                            iStart += 4;
                        }else if(tempUmfeldatum.getType().equals(CognitioTypeEnum.TYP_REAL)){ // REAL
                            temp2 = new Element(UtilsConfiguration.CC_ITEM);
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_NAME, tempUmfeldatum.getName());
                            temp2.setAttribute(UtilsConfiguration.CC_ITEM_OFFSET, "" + (iBufferOffset + iStart));

                            iStart += 4;
                            iAnzReal++;
                        }
                    }
                }
            }

            root.addContent(temp1);

            XMLOutputter out = new XMLOutputter();
            FileOutputStream writer = new FileOutputStream(savefile);
            out.output(doc, writer);
            writer.flush();
            writer.close();
        }catch(IOException exc){

            return false;
        }

        return true;
    }

    /**
     * Datei kopieren.
     *
     * @param src Quelle als URL angegeben. (Templatedatei innerhalb des Programms)
     * @param dest Ziel an welchem die Datei abgespeichert werden soll
     * @return
     *      true -> Datei erfolgreich kopiert
     *      false -> Beim kopieren ist ein Fehler aufgetreten. Datei konnte nicht angelegt werden
     */
    public static boolean copyFile(URL src, File dest)
    {
        byte[] buffer = new byte[1024];
        int read = 0;
        InputStream in = null;
        OutputStream out = null;

        try{
            in = src.openStream();
            out = new FileOutputStream(dest);

            while(true){
                read = in.read(buffer);
                if(read == -1){
                    //-1 bedeutet EOF
                    break;
                }
                out.write(buffer, 0, read);
            }
        }catch(IOException exc){

            return false;
        }finally{
            if(in != null){
                try{
                    in.close();
                    out.close();
                }catch(IOException exc){}
            }
        }

        return true;
    }

    public static int[] calcStartOfKategorien(BaseConfigurationManager cnf)
    {
        // Jede Kategorie hat ihre Startposition
        int iStart[] = new int[cnf.getCategories().size()];
        for(int i=0; i<cnf.getCategories().size(); i++){
            iStart[i] = 0;
        }

        int iBufferLast[] = new int[cnf.getDataBuffers().size()];
        for(int i=0; i<cnf.getDataBuffers().size(); i++){
            iBufferLast[i] = 0;
        }

        Iterator itBuffer = cnf.getDataBuffers().iterator();
        while(itBuffer.hasNext()){
            DataBuffer tempBuffer = (DataBuffer)itBuffer.next();

            Iterator it = tempBuffer.getKategorien().values().iterator();
            while(it.hasNext()){
                Category tempKategorie = (Category )it.next();

                int iBuffer = tempBuffer.getBufferID();
                iStart[cnf.getCategories().indexOf(tempKategorie)] = iBufferLast[iBuffer];
                iBufferLast[iBuffer] += tempKategorie.getSizeInBytes();
            }
        }

        return iStart;
    }

}
