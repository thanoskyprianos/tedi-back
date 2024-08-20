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
    private Privacy professionalPositionPrivacy;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy employmentAgencyPrivacy;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy experiencePrivacy;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy educationPrivacy;

    @Enumerated(EnumType.STRING)
    @JsonView(View.AsAdmin.class)
    private Privacy skillsPrivacy;

    public Info(Info info) { // used to construct response
        this.professionalPosition = info.professionalPositionPrivacy == Privacy.PUBLIC? info.professionalPosition : null;
        this.employmentAgency = info.employmentAgencyPrivacy == Privacy.PUBLIC? info.employmentAgency : null;
        this.experience = info.experiencePrivacy == Privacy.PUBLIC? info.experience : null;
        this.education = info.educationPrivacy == Privacy.PUBLIC? info.education : null;
        this.skills = info.skillsPrivacy == Privacy.PUBLIC? info.skills : null;

        this.id = info.id;
        this.user = info.user;

        this.professionalPositionPrivacy = info.professionalPositionPrivacy;
        this.employmentAgencyPrivacy = info.employmentAgencyPrivacy;
        this.experiencePrivacy = info.experiencePrivacy;
        this.educationPrivacy = info.educationPrivacy;
        this.skillsPrivacy = info.skillsPrivacy;
    }

    public static Info allPrivate() {
        Info info = new Info();

        info.professionalPositionPrivacy = Privacy.PRIVATE;
        info.employmentAgencyPrivacy = Privacy.PRIVATE;
        info.experiencePrivacy = Privacy.PRIVATE;
        info.educationPrivacy = Privacy.PRIVATE;
        info.skillsPrivacy = Privacy.PRIVATE;

        return info;
    }
}
