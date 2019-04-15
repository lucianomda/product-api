package com.lucianomda.rakuten.productapi.service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter @Setter
public class Category {

	private Long id;
	private Date dateCreated;
	private Date lastUpdated;
	@NotBlank
	@Size(min = 3, max = 255)
	private String name;
	@Min(1)
	private Long parentId;
	private Set<Long> childrenIds;
}
