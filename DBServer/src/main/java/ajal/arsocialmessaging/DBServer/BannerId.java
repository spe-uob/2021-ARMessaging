package ajal.arsocialmessaging.DBServer;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

public class BannerId implements Serializable {
    private String postcode;
    private Timestamp timestamp;

    public BannerId() {
    }

    public BannerId(String postcode, Timestamp timestamp) {
        this.postcode = postcode;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BannerId otherId = (BannerId) o;
        if (!Objects.equals(postcode, otherId.postcode)) return false;
        return timestamp == otherId.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postcode, timestamp);
    }

}