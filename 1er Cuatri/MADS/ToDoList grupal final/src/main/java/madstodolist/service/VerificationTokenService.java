package madstodolist.service;

import madstodolist.model.VerificationToken;
import madstodolist.model.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;


    public void saveToken(VerificationToken newToken) {
        verificationTokenRepository.save(newToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }
}
