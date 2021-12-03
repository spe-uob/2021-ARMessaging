package com.ajal.arsocialmessaging;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "messages")
public class Messages {
    @Id @GeneratedValue @Column(name = "id") @Getter @Setter
    Integer id;

    @Column(name = "message") @Getter @Setter
    String message;

    @Column(name = "ojbfilename") @Getter @Setter
    String ojbfilename;
}
