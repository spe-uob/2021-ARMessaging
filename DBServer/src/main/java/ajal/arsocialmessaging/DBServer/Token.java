package ajal.arsocialmessaging.DBServer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tokens")
@IdClass(Token.class)
public class Token implements Serializable {

    @Id @GeneratedValue @Column(name = "id") @Getter @Setter
    Integer id;

    @Column(name = "token") @Getter @Setter
    String token;

    public Token() {
        this.id = 1;
        this.token = "";
    }

    public Token(Integer id, String token) {
        this.id = id;
        this.token = token;
    }
}
