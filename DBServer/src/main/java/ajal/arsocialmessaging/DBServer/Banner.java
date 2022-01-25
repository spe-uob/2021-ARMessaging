package ajal.arsocialmessaging.DBServer;

import java.util.Date;
import java.sql.Timestamp;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "banners")
public class Banner {
    @Id @GeneratedValue @Column(name = "id") @Getter @Setter
    Integer id;

    @Column(name = "postcode") @Getter @Setter
    String postcode;

    @ManyToOne @JoinColumn(name = "message_Id") @Getter @Setter
    Message message;

    @Column(name = "created_at") @Getter @Setter
    Timestamp timestamp;

    public Banner() {
        postcode = "";
        message = new Message();
        Date date = new Date();
        timestamp = new Timestamp(date.getTime());
    }

    public Banner(String postcode, Message messageId) {
        this.postcode = postcode;
        this.message = messageId;
    }
}