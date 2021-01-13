package co.luism.ksoft.iot.utils.common;

/**
 * Created by luis on 30.01.15.
 */
public class CognitioBufferPosition {

    public final static int EVENT_SIZE = 24;

    public final static int EVENT_INDEX_POS = 0;
    public final static int EVENT_START_TIME_SECONDS_POS = 4;
    public final static int EVENT_END_TIME_S_POS = 8;
    public final static int EVENT_COUNT_POS = 12;
    public final static int EVENT_START_TIME_MSECONDS_POS = 16;
    public final static int EVENT_END_TIME_MSECONDS_POS = 18;
    public final static int EVENT_CODE_POS = 20;
    public final static int EVENT_STATUS_POS = 22;

    public final static int ENVIRONMENT_DATA_SIZE = 11;

    public final static int ENVIRONMENT_DATA_EVENT_INDEX_POS = 0;
    public final static int ENVIRONMENT_DATA_TIMESTAMP_SECONDS_POS = 5;
    public final static int ENVIRONMENT_DATA_TIMESTAMP_MSECONDS_POS = 9;

}
