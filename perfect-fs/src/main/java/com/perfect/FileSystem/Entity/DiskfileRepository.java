package com.perfect.filesystem.Entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiskfileRepository extends JpaRepository<Diskfile, String> {
	
	Page<Diskfile> findByAppid(int appid, Pageable pageable);

}