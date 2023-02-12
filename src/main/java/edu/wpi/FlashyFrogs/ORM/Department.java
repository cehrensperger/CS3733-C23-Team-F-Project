package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name="department")
public class Department {
    @Id
    @Column(nullable = false)
    @NonNull
    @Getter
    private String longName;

    @Column(nullable = false)
    @NonNull
    @Getter
    @Setter
    private String shortName;
}
