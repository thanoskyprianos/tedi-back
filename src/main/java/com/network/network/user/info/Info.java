package com.network.network.user.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.network.network.misc.View;
import com.network.network.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Setter @Getter
public class Info {
    @Id
    @JsonView(View.AsAdmin.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @OneToOne(mappedBy = "info")
    private User user;

    private String professionalPosition;
    private String employmentAgency;
    private String experience;
    private String education;
    private String skills;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy professionalPositionPrivacy = Privacy.PRIVATE;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy employmentAgencyPrivacy = Privacy.PRIVATE;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy experiencePrivacy = Privacy.PRIVATE;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy educationPrivacy = Privacy.PRIVATE;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy skillsPrivacy = Privacy.PRIVATE;

    public Info(Info info, User principal) { // used to construct response
        this.professionalPosition = info.professionalPositionPrivacy == Privacy.PUBLIC ? info.professionalPosition : null;
        this.employmentAgency = info.employmentAgencyPrivacy == Privacy.PUBLIC ? info.employmentAgency : null;
        this.experience = info.experiencePrivacy == Privacy.PUBLIC ? info.experience : null;
        this.education = info.educationPrivacy == Privacy.PUBLIC ? info.education : null;
        this.skills = info.skillsPrivacy == Privacy.PUBLIC ? info.skills : null;

        if (info.user.isConnected(principal)) { // change or keep same
            this.professionalPosition = info.professionalPositionPrivacy == Privacy.CONNECTED ? info.professionalPosition : this.professionalPosition;
            this.employmentAgency = info.employmentAgencyPrivacy == Privacy.CONNECTED ? info.employmentAgency : this.employmentAgency;
            this.experience = info.experiencePrivacy == Privacy.CONNECTED ? info.experience : this.experience;
            this.education = info.educationPrivacy == Privacy.CONNECTED ? info.education : this.education;
            this.skills = info.skillsPrivacy == Privacy.CONNECTED ? info.skills : this.skills;
        }
    }
}
