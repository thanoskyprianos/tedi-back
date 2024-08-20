package com.network.network.user.info.service;

import com.network.network.user.info.Info;
import com.network.network.user.info.Privacy;
import com.network.network.user.info.resource.InfoRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class InfoService {
    @Resource
    private InfoRepository infoRepository;

    public void saveInfo(Info info) {
        infoRepository.save(info);
    }

    public void updateInfo(Info info, Info newInfo) {
        String professionalPosition = newInfo.getProfessionalPosition();
        if (professionalPosition != null) { info.setProfessionalPosition(professionalPosition); }

        String employmentAgency = newInfo.getEmploymentAgency();
        if (employmentAgency != null) { info.setEmploymentAgency(employmentAgency); }

        String experience = newInfo.getExperience();
        if (experience != null) { info.setExperience(experience); }

        String education = newInfo.getEducation();
        if (education != null) { info.setEducation(education); }

        String skills = newInfo.getSkills();
        if (skills != null) { info.setSkills(skills); }

        saveInfo(info);
    }

    public void updateInfoPrivacy(Info info, Info newInfo) {
        Privacy professionalPositionPrivacy = newInfo.getProfessionalPositionPrivacy();
        if (professionalPositionPrivacy != null) { info.setProfessionalPositionPrivacy(professionalPositionPrivacy); }

        Privacy employmentAgencyPrivacy = newInfo.getEmploymentAgencyPrivacy();
        if (employmentAgencyPrivacy != null) { info.setEmploymentAgencyPrivacy(employmentAgencyPrivacy); }

        Privacy experiencePrivacy = newInfo.getExperiencePrivacy();
        if (experiencePrivacy != null) { info.setExperiencePrivacy(experiencePrivacy); }

        Privacy educationPrivacy = newInfo.getEducationPrivacy();
        if (educationPrivacy != null) { info.setEducationPrivacy(educationPrivacy); }

        Privacy skillsPrivacy = newInfo.getSkillsPrivacy();
        if (skillsPrivacy != null) { info.setSkillsPrivacy(skillsPrivacy); }

        saveInfo(info);
    }
}
