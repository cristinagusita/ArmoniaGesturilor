package upt.licenta.cristinagusita.armoniagesturilor.email;

public interface EmailSender {
    void send(String to, String email, String subject);
}
