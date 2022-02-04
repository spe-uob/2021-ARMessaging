package ajal.arsocialmessaging.DBServer;

import java.util.Date;
import java.sql.Timestamp;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "banners")
public class Banner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(columnDefinition = "serial") @Getter @Setter
    Integer id;

    @Column(name = "postcode") @Getter @Setter
    String postcode;

    //@ManyToOne @JoinColumn(name = "message_id") @Getter @Setter
    //Message message;

    @Column(name = "message_id") @Getter @Setter
    Integer message;

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