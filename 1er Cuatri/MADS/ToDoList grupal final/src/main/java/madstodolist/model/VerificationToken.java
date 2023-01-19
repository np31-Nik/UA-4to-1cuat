package madstodolist.model;

import javax.persistence.*;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "verification_token")
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "expiry_date")
    private Date expiryDate;

    public VerificationToken() {

    }


    public Long getId() {
        return id;
    }


    public String getToken() {
        return token;
    }

    public VerificationToken(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        this.createdDate = new Date();
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expirationInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expirationInMinutes);
        return new Date(cal.getTime().getTime());
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
}
