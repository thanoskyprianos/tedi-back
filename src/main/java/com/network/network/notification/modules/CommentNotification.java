package com.network.network.notification.modules;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.comment.Comment;
import com.network.network.misc.View;
import com.network.network.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity @Getter @Setter
@DiscriminatorValue("COMMENT")
public class CommentNotification extends Notification {
    @ManyToOne
    @JsonView(View.AsProfessional.class)
    private Comment comment;

    public CommentNotification(Comment comment, User sender, User receiver) {
        super();
        this.comment = comment;
        this.setSender(sender);
        this.setReceiver(receiver);
    }
}
