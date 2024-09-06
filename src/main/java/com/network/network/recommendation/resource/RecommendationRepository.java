package com.network.network.recommendation.resource;

import com.network.network.recommendation.JobType;
import com.network.network.recommendation.Recommendation;
import com.network.network.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
    void deleteByUserAndType(User user, JobType type);
    List<Recommendation> findAllByUserAndType(User user, JobType type);
}
