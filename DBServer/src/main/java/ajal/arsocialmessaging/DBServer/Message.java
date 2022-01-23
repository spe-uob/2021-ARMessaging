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
}

