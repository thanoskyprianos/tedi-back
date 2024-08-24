package com.network.network.notification;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
@DiscriminatorColumn(name = "`type`", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsProfessional.class)
    @Column(name = "`type`", insertable=false, updatable=false)
    private NotificationType notificationType;

    @JsonView(View.AsProfessional.class)
    @Column(name = "`read`")
    private boolean read = false;
}
