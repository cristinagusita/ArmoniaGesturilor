package upt.licenta.cristinagusita.armoniagesturilor.achievements;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private Integer songRequirement;

    @ManyToMany(mappedBy = "achievements")
    private Set<AppUser> users = new HashSet<>();

    public Achievement(String name, String description, Integer songRequirement) {
        this.name = name;
        this.description = description;
        this.songRequirement = songRequirement;
    }

    public Achievement() {

    }
}
