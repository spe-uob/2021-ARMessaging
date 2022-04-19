package ajal.arsocialmessaging.DBServer;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "messages")
public class Message {
    @Id @GeneratedValue @Column(name = "id") @Getter @Setter
    Integer id;

    @Column(name = "message") @Getter @Setter
    String message;

    @Column(name = "objfilename") @Getter @Setter
    String objfilename;

    @OneToMany(mappedBy="message") @Getter @Setter
    List<Banner> bannersList;

    public Message() {
        this.id = 1;
        this.message = "";
        this.objfilename = "";
    }

    public Message(Integer id, String message, String objfilename) {
        this.id = id;
        this.message = message;
        this.objfilename = objfilename;
    }
}

