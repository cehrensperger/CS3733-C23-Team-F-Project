package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Sanitation")
public class Sanitation {
  @Basic @Getter @Setter String type;
  @Basic @Getter @Setter String location;

  @Basic @Id @Getter @Setter int srID;
}
