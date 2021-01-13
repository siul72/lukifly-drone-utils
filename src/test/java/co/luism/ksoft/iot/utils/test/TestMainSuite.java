package co.luism.ksoft.iot.utils.test;

import co.luism.ksoft.iot.utils.common.CognitioTypeEnum;
import co.luism.ksoft.iot.utils.common.CognitioUtils;
import co.luism.ksoft.iot.utils.common.OpenOfficeDataSheetManager;
import co.luism.ksoft.iot.utils.common.UnitsEnum;
import co.luism.ksoft.iot.utils.configuration.BaseConfigurationManager;
import co.luism.ksoft.iot.utils.configuration.ConfigurationManagerUtils;
import co.luism.ksoft.iot.utils.configuration.EnumEndianess;
import co.luism.ksoft.iot.utils.configuration.ReturnCode;
import co.luism.ksoft.iot.utils.enterprise.Category;
import co.luism.ksoft.iot.utils.enterprise.DataBuffer;
import co.luism.ksoft.iot.utils.enterprise.DataTagValue;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMainSuite extends TestCase {

    private static final Logger LOG = Logger.getLogger(TestMainSuite.class);

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestMainSuite( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TestMainSuite.class );
    }

    public void test01LoadODS(){

        BaseConfigurationManager cnf = new BaseConfigurationManager();
        File file = new File("config/newbase.ods");
        ConfigurationManagerUtils.loadProjectPropertiesFromODS(cnf, file);

        System.out.println(cnf.getHardware().getValue());

    }

    public void test02LoadXml(){

        BaseConfigurationManager cnf = new BaseConfigurationManager();
        //File file = new File("config/oldbase.floxml");
        File file = new File("config/newbase.floxml");
        ConfigurationManagerUtils.loadProjectPropertiesFromXML(cnf, file);

        System.out.println(cnf.getHardware().getValue());

    }

    public void test03SaveODS(){

        BaseConfigurationManager cnf = new BaseConfigurationManager();
        File file = new File("config/base.ods");
        ConfigurationManagerUtils.loadProjectPropertiesFromODS(cnf, file);
        DataBuffer b = new DataBuffer(1,200,1,0);

        cnf.getDataBuffers().add(b);
        Category c = new Category(0, "test");
        c.setBuffer(b);
        DataTagValue t = new DataTagValue(0, "aaa", "aaa" , UnitsEnum.UNIT_NONE, CognitioTypeEnum.TYP_BIT, "aaa",
                false, true);
        c.getMyDigitalSignals().add(t);
        cnf.getCategories().add(c);

        File newFile = new File("config/newbase.ods");
        //save as another name
        ConfigurationManagerUtils.saveProjectPropertiesToODS(this.getClass(), cnf, newFile);

        try {
            CognitioUtils.compareFiles(file, newFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    public void test04SaveXML(){

        //load from file
        BaseConfigurationManager cnf = new BaseConfigurationManager();
        File file = new File("config/base.floxml");
        ConfigurationManagerUtils.loadProjectPropertiesFromXML(cnf, file);
        DataBuffer b = new DataBuffer(1,200,1,0);
        cnf.getDataBuffers().add(b);
        Category c = new Category(0, "test");
        c.setBuffer(b);
        DataTagValue t = new DataTagValue(0, "aaa", "aaa" , UnitsEnum.UNIT_NONE, CognitioTypeEnum.TYP_BIT, "aaa",
                false, true);
        c.getMyDigitalSignals().add(t);
        cnf.getCategories().add(c);

        cnf.setByteOrder(EnumEndianess.LITLE_ENDIAN);

        File newFile = new File("config/newbase.floxml");
        //save as another name
        ConfigurationManagerUtils.saveProjectPropertiesToXML(cnf, newFile);

        //then compare it
        try {
            Boolean r = CognitioUtils.compareFiles(file, newFile);

            assertTrue(r);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

    public void test05LoadBufferAndCategoriesfromODS(){
        BaseConfigurationManager cnf = new BaseConfigurationManager();
        File file = new File("config/newbase.ods");
        assertTrue(ConfigurationManagerUtils.loadProjectPropertiesFromODS(cnf, file) == ReturnCode.OK);

        //assertFalse(cnf.getDataBuffers().isEmpty());
        //assertFalse(cnf.getCategories().isEmpty());
        //assertFalse(cnf.getCategories().get(0).getMyDigitalSignals().isEmpty());

    }


    public void test06LoadTrainfromODS(){

       assertFalse(OpenOfficeDataSheetManager.getProcessDataTagSet("config/pdbase.ods").isEmpty());

    }

    public void test07LoadBufferAndSaveDataLogger(){
        BaseConfigurationManager cnf = new BaseConfigurationManager();
        File file = new File("config/newbase.ods");
        ConfigurationManagerUtils.loadProjectPropertiesFromODS(cnf, file);
        file = new File("config/newbase.floxml");
        ConfigurationManagerUtils.saveProjectPropertiesToXML(cnf, file);

        file = new File("config/newdatalogger.xml");
        ConfigurationManagerUtils.saveDiagnosticsConfigurationToDiagd(cnf, file);
    }

    public void test08LoadFromFloxmlBufferAndSaveDataLogger(){
        BaseConfigurationManager cnf = new BaseConfigurationManager();
        File file = new File("config/newbase.floxml");
        ConfigurationManagerUtils.loadProjectPropertiesFromXML(cnf, file);

        file = new File("config/newdatalogger.xml");
        ConfigurationManagerUtils.saveDiagnosticsConfigurationToDiagd(cnf, file);
    }

    public void test09GetMD5Hash(){

        String h = CognitioUtils.getMD5Hash("test");
        assertNotNull(h);
        System.out.println(h);
    }


}


