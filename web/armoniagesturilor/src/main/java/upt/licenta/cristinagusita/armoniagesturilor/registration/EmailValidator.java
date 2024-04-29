package upt.licenta.cristinagusita.armoniagesturilor.registration;

import org.springframework.stereotype.Service;
import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String s) {
        // validate email
        if (s.contains("@")) {
            return true;
        }
        return false;
    }
}
