package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "ServiceRequest")
@Inheritance(strategy = InheritanceType.JOINED)
public class Login {

  @Basic
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.ALL)
  @Column(nullable = false)
  @Getter
  @Setter
  @GeneratedValue
  long id;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String username;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String password;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String salt;
}
