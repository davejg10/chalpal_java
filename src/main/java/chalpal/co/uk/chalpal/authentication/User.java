package chalpal.co.uk.chalpal.authentication;


import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 128, nullable = false)
    private String email;

    User() {}
    public User ( String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email) {
        this.email = email;
    }
//testy
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User User = (User) o;
        return Objects.equals(this.id, User.id) && this.email == User.email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.email);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + this.id + ", email=" + this.email + '}';
    }
}