package com.web.controller.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.controller.IRestStatisticsController;
import com.web.dto.DtoStatistics;
import com.web.dto.RootEntity;
import com.web.service.ICommentService;
import com.web.service.IContentService;
import com.web.service.IStatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class RestStatisticsController implements IRestStatisticsController{
	
	@Autowired
	private  IStatisticsService statisticsService;
	
	@Override
	@GetMapping
	public RootEntity<DtoStatistics> getStatistics() {
	       return RootEntity.ok(statisticsService.getStatistics());
	 }
	
	

}
