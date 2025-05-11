package com.web.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dto.DtoStatistics;
import com.web.model.Role;
import com.web.repository.ContentRepository;
import com.web.repository.UserRepository;
import com.web.service.IStatisticsService;


@Service
public class StatisticsServiceImpl implements IStatisticsService{
	
	@Autowired
	private ContentRepository  contentRepository;

	@Autowired
	private UserRepository userRepository;
	
	
	// web statistic
	public DtoStatistics getStatistics() {
	        Long totalContentCount = contentRepository.count();
	        Long totalAuthorCount = userRepository.countByRoleIn(Role.SUPERADMIN, Role.CONTENTADMIN);
	        Long totalViewCount = contentRepository.sumViewCounts();

	    return new DtoStatistics(totalContentCount, totalAuthorCount, totalViewCount);
	}
		

}
