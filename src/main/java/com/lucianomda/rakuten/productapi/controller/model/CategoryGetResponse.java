package com.lucianomda.rakuten.productapi.controller.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter @Setter
public class CategoryGetResponse {

	private Long id;
	private Date dateCreated;
	private Date lastUpdated;
	private String name;
	private Long parentId;
	private Set<Long> childrenIds;
}
