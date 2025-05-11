package com.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DtoStatistics {
	
	 private Long totalContentCount;
	 private Long totalAuthorCount;
	 private Long totalViewCount;

}
