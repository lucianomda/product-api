package com.lucianomda.rakuten.productapi.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@MappedSuperclass
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date dateCreated;
	@Column(nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date lastUpdated;

	@PrePersist
	public void onCreate() {
		setDateCreated(new Date());
	}

	@PreUpdate
	public void onPersist() {
		setLastUpdated(new Date());
	}
}
