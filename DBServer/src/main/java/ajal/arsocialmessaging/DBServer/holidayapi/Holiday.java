package ajal.arsocialmessaging.DBServer.holidayapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Holiday {
    public final String name;
    public final String date;

    public Holiday(@JsonProperty("name") String name, @JsonProperty("date") String date) {
        this.name = name;
        this.date = date;
    }
}
