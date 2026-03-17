package com.optimagrowth.license.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.optimagrowth.license.model.Organization;

@Component
public class OrganizationRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;

    public Organization getOrganization(String organizationId){
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                		// @LoadBalanced 어노테이션으로 지원된 AOP 기능은 다음과 같이 organization-service 호스트 이름을
                		// ip address로 변경함.
                		// organization-service 호스트 네임이고,
                		// 서비스 organization-service는 my-service-app-01 : 서비스 ID 등록
                		// http://organization-service는 ip address로 변경됨/v1....
                        "http://organization-service/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
