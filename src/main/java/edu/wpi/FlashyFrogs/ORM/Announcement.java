package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "announcement")
public class Announcement {
  @Id
  @Basic
  @Column(nullable = false)
  @Getter
  @GeneratedValue
  private long id; // UUID for the announcement

  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  @Getter
  @Setter
  @NonNull
  private Date creationDate; // Date that the announcement was created

  @ManyToOne
  @Getter
  @JoinColumn(
      name = "author",
      foreignKey =
          @ForeignKey(
              name = "author_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (author) REFERENCES \"user\"(id) ON DELETE CASCADE"))
  private User author; // Author for the message

  @Basic
  @Getter
  @Setter
  @Column(nullable = false)
  @NonNull
  private String announcement;

  /** Empty constructor, required by hibernate */
  public Announcement() {}

  /**
   * Creates the announcement with the given parameters
   *
   * @param creationDate the date to create
   * @param author the author of the announcement
   * @param announcement the announcement body to create
   */
  public Announcement(@NonNull Date creationDate, User author, @NonNull String announcement) {
    this.creationDate = creationDate;
    this.author = author;
    this.announcement = announcement;
  }

  /**
   * Equals method, only returns true if the ids are equal
   *
   * @param o the object to compare this to
   * @return true if both are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // If the two are the same in memory, easy
    if (o == null || getClass() != o.getClass())
      return false; // If they aren't the same class or one is null, bad

    Announcement that = (Announcement) o; // Compare announcements

    return getId() == that.getId(); // Check IDs, do it based on that
  }

  /**
   * Hash code, hashes the ID
   *
   * @return the ID hash
   */
  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  /**
   * ToString for the announcement, returns the ID
   *
   * @return the ID as a string
   */
  @Override
  public String toString() {
    return Long.toString(this.id);
  }
}
