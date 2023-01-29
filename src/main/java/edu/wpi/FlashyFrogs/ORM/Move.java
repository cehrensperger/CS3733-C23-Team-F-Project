package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Move")
public class Move {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Getter
  @Setter
  String nodeID;

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Getter
  @Setter
  String longName;

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Temporal(TemporalType.TIMESTAMP)
  @Id
  @Getter
  @Setter
  Date moveDate;
}
