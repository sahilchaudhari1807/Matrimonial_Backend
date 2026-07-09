

package com.matrimony.backend.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matrimony.backend.Model.UsersDetail;



public interface UsersRepository extends JpaRepository<UsersDetail, Long> {
	Optional<UsersDetail> findByUserId(Long userId);
}