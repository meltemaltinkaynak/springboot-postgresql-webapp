package com.web.controller;

import com.web.dto.DtoStatistics;
import com.web.dto.RootEntity;

public interface IRestStatisticsController {
	
	public RootEntity<DtoStatistics> getStatistics();

}
