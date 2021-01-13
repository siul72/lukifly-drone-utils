package co.luism.ksoft.iot.utils.configuration;

/**
 * Created by luis on 15.10.14.
 */

public class UtilsConfiguration {


    public static final String ODS_TITLE = "Railtec";
    public static final String ODS_FAHRZEUGE = "Fahrzeuge";
    public static final String ODS_BUFFER = "Buffer";
    public static final String ODS_KATEGORIEN = "Kategorien";
    public static final String ODS_UMFELD = "Umfeldattribute";
    public static final String ODS_EREIGNISSE = "Ereignisse";

    public static final String XML_ROOT = "config";

    public static final String XML_DISP = "display";
    public static final String [] XML_DISP_LAN = {"sprache1", "sprache2", "sprache3"};
    public static final String XML_DISP_INFO = "werkstatt";

    public static final String XML_PROP = "properties";
    public static final String XML_PROP_VER = "version";
    public static final String XML_PROP_PROJEKT = "projekt";
    public static final String XML_PROP_PROJECT_HASH = "ProjektCode";
    public static final String XML_PROP_DATA_ENDIANESS = "ErrLogEndianess";
    public static final String XML_PROP_HARDWARE = "hardware";
    public static final String XML_PROP_SERVER = "server";

    public static final String XML_LOG = "errorlog";
    public static final String XML_LOG_SIZE = "size";
    public static final String XML_LOG_RING = "ringbuffer";
    public static final String XML_LOG_INTERVAL = "interval";

    public static final String XML_VEHICLE_ROOT = "fahrzeuge";
    public static final String XML_VEHICLE = "fahrzeug";
    public static final String XML_VEHICLE_MD5 = "md5";
    public static final String XML_VEHICLE_NAME = "name";
    public static final String XML_VEHICLE_POS = "position";

    public static final String XML_BUFFER_ROOT = "speicher";
    public static final String XML_BUFFER = "buffer";
    public static final String XML_BUFFER_ID = "bid";
    public static final String XML_BUFFER_SIZE = "size";
    public static final String XML_BUFFER_S = "s";
    public static final String XML_BUFFER_MS = "ms";

    public static final String XML_UMF_ROOT = "umfelddaten";
    public static final String XML_UMF_EKE = "eke";
    public static final String XML_UMF_SIGNAL = "signal";
    public static final String XML_UMF_ID = "id";
    public static final String XML_UMF_INDEX = "index";
    public static final String XML_UMF_COMMENT = "comment";
    public static final String XML_UMF_EREIGNIS = "fehler";
    public static final String XML_UMF_TYP = "typ";
    public static final String XML_UMF_SIGNED = "signed";
    public static final String XML_UMF_UMRECHNUNG = "umrechnung";
    public static final String XML_UMF_UNIT = "unit";

    public static final String XML_UMFKAT_ROOT = "umfeldkategorien";
    public static final String XML_UMFKAT_KAT = "kategorie";
    public static final String XML_UMFKAT_NAME = "name";
    public static final String XML_UMFKAT_BUFFERID = "bid";
    public static final String XML_UMFKAT_SIGNAL = "sig";
    public static final String XML_UMFKAT_ANALOG = "analog";
    public static final String XML_UMFKAT_DIGITAL = "digital";

    public static final String XML_EREIG_ROOT = "fehlerdefinitionen";
     public static final String XML_EREIG = "fehler";
    public static final String XML_EREIG_ID = "id";
    public static final String XML_EREIG_REF = "ref";
    public static final String XML_EREIG_NAME = "name";
    public static final String XML_EREIG_REACT = "xReact";
    public static final String XML_EREIG_PRE = "xPre";
    public static final String XML_EREIG_POST = "xPost";
    public static final String XML_EREIG_UICKAT = "fehlerKat";
    public static final String XML_EREIG_KAT = "kategorien";
    public static final String XML_EREIG_KATID = "kid";
    public static final String XML_EREIG_MAX = "maxFehler";
    public static final String XML_EREIG_TEXTE = "texte";

    public static final String DID_ROOT = "config";

    public static final String DID_PROJEKT = "projekt";
    public static final String DID_PROJEKT_ID = "id";

    public static final String DID_SERVER = "server";
    public static final String DID_SERVER_PORT = "port";

    public static final String DID_ERRLOG = "errlog";
    public static final String DID_ERRLOG_SIZE = "size";
    public static final String DID_ERRLOG_RINGBUF = "ringbuffer";

    public static final String DID_INTERVAL = "interval";

    public static final String DID_BUFFER_ROOT = "speicher";
    public static final String DID_BUFFER = "buffer";
    public static final String DID_BUFFER_ID = "id";
    public static final String DID_BUFFER_SIZE = "size";
    public static final String DID_BUFFER_S = "s";
    public static final String DID_BUFFER_MS = "ms";

    public static final String DID_UMFKAT_ROOT = "kategorien";
    public static final String DID_UMFKAT = "kategorie";
    public static final String DID_UMFKAT_ANZ = "anz";
    public static final String DID_UMFKAT_ID = "kid";
    public static final String DID_UMFKAT_BUFFERID = "bid";
    public static final String DID_UMFKAT_START = "start";
    public static final String DID_UMFKAT_SIZE = "size";

    public static final String DID_EREIG_ROOT = "ereignisse";
    public static final String DID_EREIG = "ereignis";

    public static final String CC_ROOT = "config";
    public static final String CC_DATA = "data";
    public static final String CC_ITEM = "item";
    public static final String CC_ITEM_NAME = "name";
    public static final String CC_ITEM_OFFSET = "offset";


    public static final int MAXBUFFER = 10;
    public static final int MAXKATEGORIEN = 100;

    public final static String EKECONFIGPFAD = "config/";
    public final static String EKEDATAPFAD = "data/";


    public static final String ODS_PROCESS_DATA_SHEET_TRAIN_DATA = "inputdataFz_t";
    public static final String EVENT_INDEX = "eidx";

    public static final String ODS_EN_TRANSLATION = "EN_Ereignisse_descriptions";
    public static final int MAX_LANGUAGES = 4;
    public static final int MIN_BUFFER_SAMPLE_RATE = 100;
    public static final int MAX_BUFFER_MS_TIME = 60000;
    public static final int MAX_NUMBER_OF_SAMPLES = 126;

}
