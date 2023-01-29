package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Node")
public class Node {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Getter
  @Setter
  String id;

  @Basic @Getter @Setter int xCoord;
  @Basic @Getter @Setter int yCoord;
  @Basic @Getter @Setter String floor;
  @Basic @Getter @Setter String building;
}
