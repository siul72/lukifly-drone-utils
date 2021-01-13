package co.luism.ksoft.iot.utils.common;

import co.luism.ksoft.iot.utils.configuration.UtilsConfiguration;
import org.apache.log4j.Logger;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by luis on 27.10.14.
 */
public class OpenOfficeDataSheetManager {

    private static final Logger LOG = Logger.getLogger(OpenOfficeDataSheetManager.class);

    public static Set<ProcessDataTag> getProcessDataTagSet(String filePath){

        File f = new File(filePath);
        return  getProcessDataTagSet(f);

    }

    public static Set<ProcessDataTag> getProcessDataTagSet(File f){

        SpreadSheet spreadsheet;
        Set<ProcessDataTag> mySet = new HashSet<>();

        if(!f.exists()){
            LOG.error("file no fount " + f.getName());
            return mySet;
        }

        try {
            spreadsheet = SpreadSheet.createFromFile(f);
        } catch (IOException e) {
            LOG.error("exception on create spreadsheet " + e);
            return mySet;
        }


        Sheet sheetTitle = spreadsheet.getSheet(UtilsConfiguration.ODS_PROCESS_DATA_SHEET_TRAIN_DATA);

        if(sheetTitle == null){

            LOG.error("ODS sheet not found " + UtilsConfiguration.ODS_PROCESS_DATA_SHEET_TRAIN_DATA);
            return mySet;
        }

        //now read all lines
        for(int i = 0; i < sheetTitle.getRowCount(); i++){
            //if first column is not numeric then not valid
            Object index = sheetTitle.getValueAt(0, i);

            if(!(index instanceof String)){
                LOG.info(String.format("row %d don't have info", i));
                continue;
            }

            Integer id;
            try {
                id  = Integer.parseInt((String) index);
            } catch(NumberFormatException ex){

                LOG.error(ex);
                continue;
            }

            ProcessDataTag t = new ProcessDataTag(id);

            t.setDataType(sheetTitle.getValueAt(1, i).toString());
            t.setName(sheetTitle.getValueAt(2, i).toString());
            t.setDescription(sheetTitle.getValueAt(3, i).toString());
            t.setNumber(sheetTitle.getValueAt(4, i).toString());

            mySet.add(t);


        }


        return mySet;


    }
}
