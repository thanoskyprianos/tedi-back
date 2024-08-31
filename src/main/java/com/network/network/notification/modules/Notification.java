package com.network.network.notification.modules;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity @Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne
    @JsonView(View.AsProfessional.class)
    private User sender;

    @ManyToOne
    @JsonView(View.AsProfessional.class)
    private User receiver;

    @CreationTimestamp
    @JsonView(View.AsProfessional.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
}
