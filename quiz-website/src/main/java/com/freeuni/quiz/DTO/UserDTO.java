package com.freeuni.quiz.DTO;

public class UserDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String imageURL;
    private String bio;
    private int id;
    private boolean isAdmin;

    public UserDTO(int id, String userName, String firstName, String lastName, String email, String imageURL, String bio) {
        this.id=id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imageURL = imageURL;
        this.bio = bio;
        this.isAdmin = false;
    }

    public UserDTO(int id, String userName, String firstName, String lastName, String email, String imageURL, String bio, boolean isAdmin) {
        this.id=id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imageURL = imageURL;
        this.bio = bio;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isAdmin() {return isAdmin;}
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
