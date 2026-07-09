package com.matrimony.backend.Service;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matrimony.backend.DTO.IncomingRequestDTO;
import com.matrimony.backend.DTO.MatchDTO;
import com.matrimony.backend.DTO.MatchProfileDTO;
import com.matrimony.backend.Model.Interest;
import com.matrimony.backend.Model.UsersDetail;
import com.matrimony.backend.Repo.InterestRepo;
import com.matrimony.backend.Repo.UsersRepository;

@Service
public class InterestService {
   
	
	@Autowired
	private InterestRepo interestRepo;
	
	@Autowired
	private UsersRepository userRepo;
	
	// =====================================================
    // Send interest request from one user to another
    // Prevents self requests and duplicate requests
    // =====================================================
	
	public Optional<Interest> sendInterest(Long fromUserId, Long toUserId) {

	    if (fromUserId.equals(toUserId)) {
	        throw new RuntimeException("You cannot send request to yourself");
	    }

	    Optional<Interest> interestObject =
	            interestRepo.findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
	                    fromUserId,
	                    toUserId,
	                    toUserId,
	                    fromUserId
	            );

	    if (interestObject.isPresent()) {

	        Interest interest = interestObject.get();

	        if ("REJECTED".equals(interest.getStatus())) {

	            // Update existing rejected request
	            interest.setFromUserId(fromUserId);
	            interest.setToUserId(toUserId);
	            interest.setStatus("PENDING");

	            return Optional.of(interestRepo.save(interest));
	        }

	        throw new RuntimeException("Interest request already exists");
	    }

	    // Create new interest
	    Interest interest = new Interest();
	    interest.setFromUserId(fromUserId);
	    interest.setToUserId(toUserId);
	    interest.setStatus("PENDING");

	    return Optional.of(interestRepo.save(interest));
	}
	
	
	
	 // =====================================================
    // Get all requests sent by current user
    // =====================================================
	public List<Interest> getSentRequests(Long currentUserId){
		List<Interest> list=interestRepo.findByFromUserId(currentUserId);
		
		return list;
	}
	
	// get all requests
	 // =====================================================
    // Get all requests received by current user
    // =====================================================
      public List<Interest> getAllRequests(Long currentUserId){
		
		List<Interest> list=interestRepo.findByToUserId(currentUserId);
		
		return list;
	}
      // =====================================================
      // Accept an incoming interest request
      // Changes status from PENDING to ACCEPTED
      // =====================================================
      
      public Optional<Interest> AcceptRequest(Long fromUserId, Long toUserId) {

    	    Optional<Interest> acceptRequest =
    	            interestRepo.findByFromUserIdAndToUserId(fromUserId, toUserId);

    	    System.out.println("Accept API Hit");
    	    System.out.println("fromUserId = " + fromUserId);
    	    System.out.println("toUserId = " + toUserId);
    	    if (acceptRequest.isPresent()) {

    	        Interest interest = acceptRequest.get();

    	        interest.setStatus("ACCEPTED");

    	        Interest savedInterest = interestRepo.save(interest);

    	        return Optional.of(savedInterest);
    	    }

    	    throw new RuntimeException("Interest request is not present");
    	}
      
   // =====================================================
      // Reject an incoming interest request
      // Changes status from PENDING to REJECTED
      // =====================================================
      public Optional<Interest> RejectRequest(Long fromUserId,Long toUserId){
    	  Optional<Interest> RejectRequest=interestRepo.findByFromUserIdAndToUserId(fromUserId, toUserId);
    	  
    	  if(RejectRequest.isPresent()) {
    		  Interest interest=RejectRequest.get();
    		  
    		  interest.setStatus("REJECTED");
    		  
    		 // interestRepo.save(interest);
    		  
    		  return Optional.of(interestRepo.save(interest));
    	  }
    	  else {
    		  throw new RuntimeException("interest request is Rejected");
    	  }
      }
      
      // =====================================================
      // Get all accepted matches for current user
      // Returns profile information as MatchDTO
      // =====================================================
      public List<MatchDTO> getMatches(Long currentUserId){
    	  
    	  List<Interest> matches=new ArrayList<>();
    	  
    	  List<MatchDTO> result = new ArrayList<>();
    	  
    	  List<Interest> senderMatches=interestRepo.findByStatusAndFromUserId("ACCEPTED", currentUserId);
    	  List<Interest> ReceiverMatches=interestRepo.findByStatusAndToUserId("ACCEPTED", currentUserId);
    	  
    	 
    	  
    	  matches.addAll(senderMatches);
    	  matches.addAll(ReceiverMatches);
    	  
    	  System.out.println("senderMatches = " + senderMatches.size());
    	  System.out.println("receiverMatches = " + ReceiverMatches.size());
    	  System.out.println("total matches = " + matches.size());
    	  
    	  for(Interest interest:matches) {
    		  Long OtherUserID;
    		  if(currentUserId.equals(interest.getFromUserId())) {
    			   OtherUserID=interest.getToUserId();
    			   System.out.println("OtherUserID = " + OtherUserID);
    		  }else {
    		  OtherUserID=interest.getFromUserId();
    		  System.out.println("OtherUserID = " + OtherUserID);
    		  }
    		  
    		  UsersDetail user=userRepo.findByUserId(OtherUserID).orElse(null);
    		  System.out.println("User = " + user);
    		  if(user!=null) {
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
    	  }
    	  
    	  return result;
    	  
    	  
    	  
      }
      // =====================================================
      // Get all pending requests received by current user
      // Returns sender profile information
      // =====================================================
      public List<IncomingRequestDTO> getIncomingRequest(Long currentUserId){
    	  List<IncomingRequestDTO> result=new ArrayList<>();
    	  List<Interest> PendingInterest=interestRepo.findByStatusAndToUserId("PENDING", currentUserId);
    	  System.out.println(currentUserId);
		  System.out.println(PendingInterest);
		  System.out.println(PendingInterest.size());
    	  
    	  for(Interest interest:PendingInterest) {
    		  Long otherUserId = interest.getFromUserId();
    		
    		  UsersDetail user=userRepo.findByUserId(otherUserId).orElse(null);
    		  System.out.println("otherUserId = " + otherUserId);
    		  System.out.println("user = " + user);
    		  if(user!=null) {
    		  IncomingRequestDTO dto=new IncomingRequestDTO(
    				  user.getId(),
    				  user.getUserId(),
     				 user.getName(),
     				  user.getAge(),
     				  user.getGender(),
     				  user.getCity(),
     				  user.getBio());
    		  
    		         result.add(dto);
    		  }		 
    		  
    	  }
    	  return result;
    	 
    	
      }
      // =====================================================
      // Check whether two users are matched
      // Returns true if request status is ACCEPTED
      // =====================================================
      public boolean isMatched(Long user1,Long user2) {
    	  Optional<Interest> interest=interestRepo.findByFromUserIdAndToUserId(user1, user2);
    	  Optional<Interest> interest1=interestRepo.findByFromUserIdAndToUserId(user2, user1);
    	  if(interest.isPresent()) {
    		  Interest obj = interest.get();
    		  if(obj.getStatus().equals("ACCEPTED")) {
    		         return true;
    		  }
    	  }
    	  if (interest1.isPresent()) {

    	        Interest obj1 = interest1.get();

    	        if (obj1.getStatus().equals("ACCEPTED")) {
    	            return true;
    	        }
    	    }
    	 return false;
      }
      
   // =====================================================
   // Fetch all user profiles except the logged-in user
   // Determines the relationship between the logged-in user
   // and each profile (NONE, SENT, RECEIVED, MATCHED)
   // Returns profile information as MatchProfileDTO
   // =====================================================
      
      public List<MatchProfileDTO> getAllProfilesForMatching(Long currentUserId) {

    	    List<MatchProfileDTO> result = new ArrayList<>();

    	    List<UsersDetail> users = userRepo.findAll();

    	    for (UsersDetail user : users) {

    	        // Skip logged-in user's own profile
    	        if (currentUserId.equals(user.getUserId())) {
    	            continue;
    	        }

    	        Optional<Interest> profileMatch =
    	                interestRepo.findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
    	                        currentUserId,
    	                        user.getUserId(),
    	                        user.getUserId(),
    	                        currentUserId
    	                );

    	        String relationship;
    	        String status;

    	        if (profileMatch.isPresent()) {

    	            Interest interest = profileMatch.get();

    	            status = interest.getStatus();

    	            Long senderId = interest.getFromUserId();

    	            if ("ACCEPTED".equals(status)) {

    	                relationship = "MATCHED";

    	            } 
    	            else if("REJECTED".equals(status)){
    	            	relationship = "NONE";
    	            	
    	            } else if (senderId.equals(currentUserId)) {

    	                relationship = "SENT";

    	            } else {

    	                relationship = "RECEIVED";
    	            }

    	        } else {

    	            relationship = "NONE";
    	            status = null;
    	        }

    	        MatchProfileDTO dto = new MatchProfileDTO(
    	                user.getUserId(),
    	                user.getName(),
    	                user.getAge(),
    	                user.getCity(),
    	                null,
    	                relationship,
    	                status
    	        );

    	        result.add(dto);
    	    }

    	    return result;
    	}
}
