package com.freeuni.quiz.bean;

public class User {
        private int id;
        private String hashPassword;
        private String firstName;
        private String lastName;
        private String userName;
        private String email;
        private String imageURL;
        private String bio;
        private String salt;

        public User() { }

        public User(int id, String hashPassword, String firstName, String lastName,
                    String userName, String email, String imageURL, String bio,String salt) {
            this.id = id;
            this.hashPassword = hashPassword;
            this.firstName = firstName;
            this.lastName = lastName;
            this.userName = userName;
            this.email = email;
            this.imageURL = imageURL;
            this.bio = bio;
            this.salt = salt;
        }

        public void setSalt(String salt) { this.salt = salt; }

        public String getSalt() { return salt; }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getHashPassword() {
            return hashPassword;
        }

        public void setHashPassword(String hashPassword) {
            this.hashPassword = hashPassword;
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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

}
