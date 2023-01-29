package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locationName")
public class locationName {
  @Id @Getter @Setter String longName;

  @Basic @Getter @Setter String shortName;
  @Basic @Getter @Setter String locationType;
}
