package com.matrimony.backend.DTO;



public class IncomingRequestDTO {

    private Long id;          // UsersDetail id (Profile id)
    private Long userId;      // Users table id (Login id)

    private String name;
    private Integer age;
    private String gender;
    private String city;
    private String bio;

    public IncomingRequestDTO() {
    }

    public IncomingRequestDTO(Long id,
                              Long userId,
                              String name,
                              Integer age,
                              String gender,
                              String city,
                              String bio) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.city = city;
        this.bio = bio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}