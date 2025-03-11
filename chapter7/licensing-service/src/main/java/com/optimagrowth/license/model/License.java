package com.optimagrowth.license.model;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@Entity
@Table(name="licenses")
// @JsonInclude는 Jackson 라이브러리에서 제공하는 어노테이션으로, 
// 객체를 JSON으로 직렬화할 때 특정 조건에 따라 필드를 포함하거나 제외하도록 설정합니다.
// 그중 JsonInclude.Include.NON_NULL은 필드 값이 null인 경우,
// 해당 필드를 JSON 출력에서 제외하는 데 사용됩니다.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class License extends RepresentationModel<License> {

	@Id
	@Column(name = "license_id", nullable = false)
	private String licenseId;
	private String description;
	@Column(name = "organization_id", nullable = false)
	private String organizationId;
	@Column(name = "product_name", nullable = false)
	private String productName;
	@Column(name = "license_type", nullable = false)
	private String licenseType;
	@Column(name="comment")
	private String comment;
	@Transient
	private String organizationName;
	@Transient
	private String contactName;
	@Transient
	private String contactPhone;
	@Transient
	private String contactEmail;


	public License withComment(String comment){
		this.setComment(comment);
		return this;
	}
}