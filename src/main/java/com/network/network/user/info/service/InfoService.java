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

    public Info saveInfo(Info info) {
        return infoRepository.save(info);
    }

    public void updateInfo(Info info, Info newInfo) {
        String experience = newInfo.getExperience();
        if (experience != null) {
            info.setExperience(experience);
        }

        String education = newInfo.getEducation();
        if (education != null) {
            info.setEducation(education);
        }

        String skills = newInfo.getSkills();
        if (skills != null) {
            info.setSkills(skills);
        }

        infoRepository.save(info);
    }

    public void updateExperiencePrivacy(Info info, Privacy privacy) {
        info.setExperiencePrivacy(privacy);
        infoRepository.save(info);
    }

    public void updateEducationPrivacy(Info info, Privacy privacy) {
        info.setEducationPrivacy(privacy);
        infoRepository.save(info);
    }

    public void updateSkillsPrivacy(Info info, Privacy privacy) {
        info.setSkillsPrivacy(privacy);
        infoRepository.save(info);
    }
}
