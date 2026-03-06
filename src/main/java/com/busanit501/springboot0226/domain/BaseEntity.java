package com.busanit501.springboot0226.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 파일설명 : 시간 자동 기록을 담당하는 부모 클래스

// 공통 매핑 정보가 필요할 때 사용하는 부모 클래스
// 이 어노테이션이 붙은 클래스를 상속받으면, 자식 엔티티들은 부모의 필드(컬럼)를 자신의 컬럼으로 인식하게 됩니다.
@MappedSuperclass
// 엔티티의 변화(생성, 수정 등)를 감시하는 '리스너'를 등록하는 기능
// 엔티티 객체에 특정 이벤트(저장, 수정, 삭제 등)가 발생할 때, 이를 감지해서 특정 로직을 실행하도록 연결
// AuditingEntityListener.class와 함께 써서 시간을 자동으로 기록
@EntityListeners(value = {AuditingEntityListener.class})
// 모든 필드의 Getter 메서드를 자동 생성합니다.
// getId(), getName() 등을 직접 안 짜도 됨
@Getter
abstract class BaseEntity { // 설계 클래스 목적으로 사용 할 예정 // abstract 추상클래스

    // 생성 시간 필드
    @CreatedDate // 생성되는 시점의 날짜를 쓰겠다. 엔티티가 생성되어 저장될 때 시간이 자동 저장됩니다.
    @Column(name = "regDate", updatable = false)
    private LocalDateTime regDate;

    // 수정 시간 필드
    @LastModifiedDate // 마지막 수정된 시점의 날짜를 쓰겠다. 조회한 엔티티의 값을 변경할 때 시간이 자동 저장됩니다.
    @Column(name = "modDate")
    private LocalDateTime modDate;

}
