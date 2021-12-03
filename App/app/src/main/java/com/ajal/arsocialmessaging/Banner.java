package com.ajal.arsocialmessaging;

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

    @ManyToOne @JoinColumn(name = "messageId") @Getter @Setter
    Message message;

    @Column(name = "created_at") @Getter @Setter
    Timestamp timestamp;
}
