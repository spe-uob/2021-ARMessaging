package ajal.arsocialmessaging.DBServer;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "messages")
@SequenceGenerator(
        name = "seqid-gen",
        sequenceName = "messages_id_seq",
        initialValue = 1,
        allocationSize = 1
)
public class Message {

    // REFERENCE: https://stackoverflow.com/questions/38316680/creating-sequence-id-in-hibernate 27/02/2022 23:05
    // The sequence is required for truncate() to reset the autoincrement
    // when all of the entities in the table are removed
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqid-gen")
    @Column(name = "id") @Getter @Setter
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

