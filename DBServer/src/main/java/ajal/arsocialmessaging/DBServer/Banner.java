package ajal.arsocialmessaging.DBServer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "banners")
@IdClass(BannerId.class)
public class Banner {

    @Id
    @Column(name = "postcode", length = 255) @Getter @Setter
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