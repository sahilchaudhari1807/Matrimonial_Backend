package com.matrimony.backend.DTO;
public class MatchProfileDTO {

    private Long userId;
    private String name;
    private Integer age;
    private String city;
    private String photo;
    private String relationship;
    private String status;

    // Default Constructor
    public MatchProfileDTO() {
    }

    // Parameterized Constructor
    public MatchProfileDTO(Long userId, String name, Integer age, String city,
                           String photo, String relationship, String status) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.city = city;
        this.photo = photo;
        this.relationship = relationship;
        this.status = status;
    }

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}