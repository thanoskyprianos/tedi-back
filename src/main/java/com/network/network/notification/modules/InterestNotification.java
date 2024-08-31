package com.network.network.notification.modules;

import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.post.Post;
import com.network.network.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@DiscriminatorValue("INTEREST")
public class InterestNotification extends Notification {
    @ManyToOne
    @JsonView(View.AsProfessional.class)
    private Post post;

    public InterestNotification(Post post, User sender, User receiver) {
        super();
        this.post = post;
        this.setSender(sender);
        this.setReceiver(receiver);
    }
}
