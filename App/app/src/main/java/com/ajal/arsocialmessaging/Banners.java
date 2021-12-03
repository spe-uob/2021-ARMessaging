package com.ajal.arsocialmessaging;

import java.sql.Timestamp;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "banners")
public class Banners {
    @Id @GeneratedValue @Column(name = "id") @Getter @Setter
    Integer id;

    @Column(name = "postcode") @Getter @Setter
    String postcode;

    @Column(name = "messageId") @Getter @Setter
    Integer messageId;

    @Column(name = "created_at") @Getter @Setter
    Timestamp timestamp;
}
