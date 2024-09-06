package com.network.network.recommendation.resource;

import com.network.network.recommendation.JobType;
import com.network.network.recommendation.Recommendation;
import com.network.network.user.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecommendationService {
    @Resource
    private RecommendationRepository recommendationRepository;

    @Transactional
    public void deleteRecommendations(User user, JobType type) {
        recommendationRepository.deleteByUserAndType(user, type);
    }

    public void saveRecommendations(List<Recommendation> recommendations) {
        recommendationRepository.saveAll(recommendations);
    }
}
