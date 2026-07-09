package com.matrimony.backend.Repo;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matrimony.backend.Model.Interest;
public interface InterestRepo extends JpaRepository<Interest,Long>{

	
	public Optional<Interest> findByFromUserIdAndToUserId(Long fromUserId,Long toUserId) ;
	
	public List<Interest> findByToUserIdAndStatus(Long currentUserId,String Status);
		
		
	public List<Interest> findByFromUserId(Long currentUserId);
	
	
	public List<Interest> findByToUserId(Long currentUserId);
	
	public List<Interest> findByStatusAndFromUserId(String status, Long fromUserId);

	public List<Interest> findByStatusAndToUserId(String status, Long toUserId);
	
	
	Optional<Interest> findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
	        Long fromUserId1,
	        Long toUserId1,
	        Long fromUserId2,
	        Long toUserId2
	);
	
	
	
	
}
