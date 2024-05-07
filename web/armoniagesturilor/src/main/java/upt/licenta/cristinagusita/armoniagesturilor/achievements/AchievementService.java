package upt.licenta.cristinagusita.armoniagesturilor.achievements;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUserRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @PostConstruct
    public void init() {

        String[] names = {"First Song", "Beginner", "Bronze", "Silver", "Gold", "Diamond"};
        int[] requirements = {1, 5, 10, 25, 50, 100};
        String[] descriptions = {
                "Create your first song!",
                "Create 5 songs!",
                "Create 10 songs!",
                "Create 25 songs!",
                "Create 50 songs!",
                "Create 100 songs!"
        };

        for (int i = 0; i < names.length; i++) {
            if (achievementRepository.findByName(names[i]).isEmpty()) {
                Achievement achievement = new Achievement(names[i], descriptions[i], requirements[i]);
                achievementRepository.save(achievement);
            }
        }
    }

    public void assignAchievementToUser(String achievementName, AppUser user) {
        Achievement achievement = achievementRepository.findByName(achievementName)
                .orElseThrow(() -> new RuntimeException("Achievement not found!"));
        user.getAchievements().add(achievement);
        appUserRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String checkAndAssignAchievements(AppUser user) {

        List<Achievement> achievements = achievementRepository.findAll();
        String achievementAssigned = "None";
        int userSongCount = user.getSongs().size();

        System.out.println(userSongCount);

        for (Achievement achievement : achievements) {
            if (userSongCount == achievement.getSongRequirement()) {
                achievementAssigned = achievement.getName();
            }
        }
        return achievementAssigned;
    }

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public List<Achievement> getUserAchievements(AppUser user) {

        List<Achievement> achievements = achievementRepository.findAll();
        List<Achievement> obtainedAchivements = new ArrayList<Achievement>();
        int userSongCount = user.getSongs().size();

        for (Achievement achievement : achievements) {
            if (userSongCount >= achievement.getSongRequirement()) {
                obtainedAchivements.add(achievement);
            }
        }
        return obtainedAchivements;
    }

    public List<String> getAchievementNames(List<Achievement> achievements){
        List<String> achievementNames = new ArrayList<>();
        for (Achievement achievement : achievements) {
            achievementNames.add(achievement.getName());
        }
        return achievementNames;
    }


    public void assignAchievementToUser(Achievement achievement, AppUser user) {
        user.getAchievements().add(achievement);
        appUserRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAchievementStatus(AppUser user) {
        // remove achievements in case the user no longer meets the requirements
        Set<Achievement> achievements = user.getAchievements();
        int userSongCount = user.getSongs().size() - 1;

        Iterator<Achievement> iterator = achievements.iterator();
        while (iterator.hasNext()) {
            Achievement achievement = iterator.next();
            if (userSongCount < achievement.getSongRequirement()) {
                iterator.remove();
            }
        }

        appUserRepository.save(user);
    }

}

