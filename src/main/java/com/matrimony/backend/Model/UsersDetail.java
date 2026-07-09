package com.matrimony.backend.Model;
import jakarta.persistence.*;



@Entity
public class UsersDetail {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String name;

	    private Integer age;

	    private String gender;

	    private String bio;

	    private String city;

	    private Long userId;
	    // GETTERS AND SETTERS

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
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

	    public String getBio() {
	        return bio;
	    }

	    public void setBio(String bio) {
	        this.bio = bio;
	    }

	    public String getCity() {
	        return city;
	    }

	    public void setCity(String city) {
	        this.city = city;
	    }
	    public Long getUserId() {
	    	return userId;
	    }
	    public void setUserId(Long userId) {
	    	this.userId=userId;
	    }
}
