package upt.licenta.cristinagusita.armoniagesturilor.song;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;
import upt.licenta.cristinagusita.armoniagesturilor.appuser.AppUser;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private AppUser user;

    private String fileName;
    private String contentType;

    private Boolean isPublic;

    @ManyToMany
    @JoinTable(
            name = "song_likes",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<AppUser> likedByUsers = new HashSet<>();

    @Lob
    private byte[] data;

    public void togglePublicPrivate() {
        this.isPublic = !this.isPublic;
    }

    // Convenience method to add a like
    public void addLike(AppUser user) {
        this.likedByUsers.add(user);
    }

    // Convenience method to remove a like
    public void removeLike(AppUser user) {
        this.likedByUsers.remove(user);
    }

    // Method to get the number of likes
    public int getLikesCount() {
        return this.likedByUsers.size();
    }
}
