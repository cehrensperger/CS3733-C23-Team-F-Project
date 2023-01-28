package edu.wpi.FlashyFrogs.ORM;

import lombok.Getter;
import lombok.Setter;

public class Node {
    @Getter @Setter String id;
    @Getter @Setter int xCoord;
    @Getter @Setter int yCoord;
    @Getter @Setter String floor;
    @Getter @Setter String building;
}
