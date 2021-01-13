package co.luism.ksoft.iot.utils.enterprise;

import co.luism.ksoft.iot.utils.common.CognitioTypeEnum;
import co.luism.ksoft.iot.utils.common.UnitsEnum;

/**
 * Created by luis on 15.10.14.
 */
public class DataTagEke extends DataTagValue
{

    public DataTagEke(){

    }

    public DataTagEke(Integer id, String sUmfeldname, String sKommentar, UnitsEnum sUnit, CognitioTypeEnum sTyp, String sUmwandlung, boolean xSigned, boolean xAlarm)
    {
        super(id, sUmfeldname, sKommentar, sUnit, sTyp, sUmwandlung, xSigned, xAlarm);
    }


    @Override
    public String toString() {
        return getName();
    }

}
