package com.iitj.projectplatform;

import jakarta.persistence.*;

@Entity
@Table(name = "Attempts")
public class Attempts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private int attempts;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(final int attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "Attempts{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", attempts=" + attempts +
                '}';
    }

}
