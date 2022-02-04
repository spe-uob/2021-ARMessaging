package ajal.arsocialmessaging.DBServer;

import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "banners")
@IdClass(Banner.class)
public class Banner implements Serializable {

    @Id
    @Column(name = "postcode") @Getter @Setter
    String postcode;

    @Column(name = "message_id") @Getter @Setter
    Integer message;

    @Id
    @Column(name = "created_at") @Getter @Setter
    Timestamp timestamp;

    public Banner() {
        postcode = "";
        message = 1;
        Date date = new Date();
        timestamp = new Timestamp(date.getTime());
    }

    public Banner(String postcode, Integer messageId) {
        this.postcode = postcode;
        this.message = messageId;
        Date date = new Date();
        this.timestamp = new Timestamp(date.getTime());
    }
}