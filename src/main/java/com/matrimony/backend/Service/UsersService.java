package com.matrimony.backend.Service;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matrimony.backend.DTO.MatchDTO;
import com.matrimony.backend.Model.UsersDetail;
import com.matrimony.backend.Repo.UsersRepository;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    // GET ALL USERS
    public List<MatchDTO> getAllUsers() {
    	List<UsersDetail> users = usersRepository.findAll();

    	List<MatchDTO> result = new ArrayList<>();
    	
    	for(UsersDetail user : users) {

    	    MatchDTO dto = new MatchDTO(
    	        user.getId(),
    	        user.getName(),
    	        user.getAge(),
    	        user.getGender(),
    	        user.getCity(),
    	        user.getBio(),
    	        user.getUserId()
    	    );

    	    result.add(dto);
    }
    	return result;
    }
    // GET USER BY ID
    public UsersDetail getUserById(Long id) {

        return usersRepository.findByUserId(id)
                .orElse(null);
    }

    // CREATE USER
    public UsersDetail createUser(UsersDetail user,Long currentUserId) {
    	System.out.println("CREATE USER HIT");
    	System.out.println("currentUserId = " + currentUserId);
    	
    	user.setUserId(currentUserId);  
        return usersRepository.save(user);
    }

    // UPDATE USER
    public UsersDetail updateUser(
            Long id,
            UsersDetail updatedUser) {

        UsersDetail existingUser =
                usersRepository.findById(id)
                .orElse(null);

        if(existingUser == null) {
            return null;
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setCity(updatedUser.getCity());

        return usersRepository.save(existingUser);
    }
}