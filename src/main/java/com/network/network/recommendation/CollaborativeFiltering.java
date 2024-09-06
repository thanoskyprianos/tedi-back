package com.network.network.recommendation;

import com.network.network.post.Post;
import com.network.network.post.service.PostService;
import com.network.network.recommendation.resource.RecommendationService;
import com.network.network.user.User;
import com.network.network.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Configuration
@EnableScheduling
public class CollaborativeFiltering {
    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Resource
    private RecommendationService recommendationService;

    @Async
    @Scheduled(fixedDelay = 3600000) // execution time + 1 hour
    public void postMatrixFactorization() {
        System.out.println(ANSI_RED + "EXECUTING POST FACTORIZATION" + ANSI_RESET);

        List<User> users = userService
                .getAllUsers()
                .stream()
                .sorted(Comparator.comparingInt(User::getId))
                .toList();

        List<Post> posts = postService
                .getAllPosts()
                .stream()
                .sorted(Comparator.comparingInt(Post::getId))
                .toList();

        double[][] recs = new double[users.size()][posts.size()];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            for (int j = 0; j < posts.size(); j++) {
                Post post = posts.get(j);
                recs[i][j] = 0;

                recs[i][j] += post.getViewers().contains(user) ? 1 : 0;
                recs[i][j] += post.getLikedBy().contains(user) ? 2 : 0;

                long commentCount = post.getComments()
                                        .stream()
                                        .filter(user.getComments()::contains)
                                        .count();

                recs[i][j] += 3 * commentCount;
            }
        }

        long maxCommentCount = posts.stream()
                                    .sorted(Comparator.comparing(post -> post.getComments().size()))
                                    .toList()
                                    .get(0)
                                    .getComments()
                                    .size() + 1; // + 1 in case of 0

        for (int i = 0; i < recs.length; i++) {
            for (int j = 0; j < recs[i].length; j++) {
                recs[i][j] /= maxCommentCount; // normalize
            }
        }

        Object[] res = matrixFactorization(recs, 2);
        double[][] pred = mult((double[][]) res[0], (double[][]) res[1]);

        insertRecommendations(users, posts, pred, JobType.POST);

        System.out.println(ANSI_RED + "FINISHED POST RECOMMENDATIONS" + ANSI_RESET);
    }

    @Async
    @Scheduled(fixedDelay = 3600000) // execution time + 1 hour
    public void jobOfferMatrixFactorization() {
        System.out.println(ANSI_RED + "EXECUTING JOB OFFER FACTORIZATION" + ANSI_RESET);

        List<User> users = userService
                .getAllUsers()
                .stream()
                .sorted(Comparator.comparingInt(User::getId))
                .toList();

        List<Post> posts = postService
                .getAllJobOffers()
                .stream()
                .sorted(Comparator.comparingInt(Post::getId))
                .toList();

        double[][] recs = new double[users.size()][posts.size()];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            for (int j = 0; j < posts.size(); j++) {
                Post post = posts.get(j);

                recs[i][j] = post.getViewers().contains(user) ? 1 : 0;
            }
        }

        Object[] res = matrixFactorization(recs, 2);
        double[][] pred = mult((double[][]) res[0], (double[][]) res[1]);

        insertRecommendations(users, posts, pred, JobType.JOB_OFFER);

        System.out.println(ANSI_RED + "FINISHED JOB OFFER RECOMMENDATIONS" + ANSI_RESET);
    }

    public Object[] matrixFactorization(double[][] r, int k) {
        double[][] v = new double[r.length][k];
        double[][] f = new double[k][r[0].length];

        for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < k; j++) {
                v[i][j] = Math.random();
            }
        }

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < r[0].length; j++) {
                f[i][j] = Math.random();
            }
        }

        int steps = 2500;
        double h = 0.001;
        double l = 0.001;

        return this.matrixFactorization(r, v, f, k, steps, h, l);
    }

    private Object[] matrixFactorization(
            double[][] r, double[][] v, double[][] f, int k, int steps, double h, double l
    ) {
        double prev = 0;

        while(steps-- > 0) {
            for (int i = 0; i < r.length; i++) {
                for (int j = 0; j < r[0].length; j++) {
                    if (r[i][j] > 0) {
                        double eij = r[i][j] - dot(v[i], column(f, j));

                        for (int m = 0; m < k; m++) {
                            v[i][m] = v[i][m] + h * (2 * eij * f[m][j] - l * v[i][m]);
                            f[m][j] = f[m][j] + h * (2 * eij * v[i][m] - l * f[m][j]);
                        }
                    }
                }
            }

            double sse = 0;
            for (int i = 0; i < r.length; i++) {
                for (int j = 0; j < r[0].length; j++) {
                    if (r[i][j] > 0) {
                        double eij = r[i][j] - dot(v[i], column(f, j));
                        sse += eij * eij;

                        for (int m = 0; m < k; m++) {
                            sse += l / 2 * (v[i][m] * v[i][m] + f[m][j] * f[m][j]);
                        }
                    }
                }
            }

            double rmse = Math.sqrt(sse / r.length);

            if (Math.abs(rmse - prev) < 0.0001) {
                break;
            }

            prev = rmse;
        }

        return new Object[]{v, f};
    }

    private void insertRecommendations(List<User> users, List<Post> posts, double[][] recommendations, JobType jobType) {
        for (int i = 0; i < recommendations.length; i++) {
            User user = users.get(i);

            System.out.println(ANSI_RED + "INSERTING " + jobType.toString() + " RECOMMENDATIONS FOR USER: " + ANSI_RESET + user.getId());
            insertRecommendations(user, posts, recommendations[i], jobType);
        }
    }

    private void insertRecommendations(User user, List<Post> posts, double[] recommendations, JobType jobType) {
        List<Recommendation> recommendationList = new LinkedList<>();

        for (int i = 0; i < recommendations.length; i++) {
            recommendationList.add(new Recommendation(0, user, posts.get(i), recommendations[i], jobType));
        }

        recommendationService.deleteRecommendations(user, jobType);
        recommendationService.saveRecommendations(recommendationList);
    }

    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }

        return sum;
    }

    private double[] column(double[][] a, int col) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i][col];
        }

        return res;
    }

    private double[][] mult(double[][] a, double[][] b) {
        double[][] res = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                res[i][j] = 0;
                for (int k = 0; k < a[0].length; k++) {
                    res[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return res;
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
}
