package edu.wpi.FlashyFrogs.Accounts;

import edu.wpi.FlashyFrogs.ORM.User;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


public enum CurrentUserEntity {
    CURRENT_USER; // The current user
    private User currentUser;

    public User getCurrentuser() {
        return this.currentUser;
    }

    public boolean getAdmin() {
        return currentUser.getEmployeeType().ordinal() == 0;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}

