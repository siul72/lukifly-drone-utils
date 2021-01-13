package co.luism.ksoft.iot.utils.common;

/**
 * Created by luis on 27.10.14.
 */
public class ProcessDataTag {
//0	int16_t	wOiltemp	Motoroeltemperatur	15

    private final Integer id;
    private String dataType;
    private String name;
    private String description;
    private String number;

    ProcessDataTag(Integer id){
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
