package com.busanit501.springboot0226.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Primary;

// 파일설명 : 실제 게시판 데이터를 담고 DB와 연결되는 핵심 엔티티


// 설명
// @Entity: JPA가 관리할 데이터베이스 테이블과 매핑될 클래스임을 선언합니다.
// @Id: 테이블의 기본 키(Primary Key)에 매핑합니다.
// @GeneratedValue: 기본 키 값을 DB가 자동으로 생성하도록 위임합니다. (예: MySQL의 Auto Increment)
// EntityManager: 영속성 컨텍스트를 관리하고 CRUD 작업을 수행하는 핵심 객체입니다.
// EntityTransaction: DB의 상태를 변화시키는 작업(추가, 수정, 삭제)은 반드시 트랜잭션 안에서 이루어져야 합니다.


// 데이터베이스 테이블과 매핑되는 객체다
// 클래스 이름을 테이블 이름으로 인식 : 여기서는 Board
@Entity
// 추가 기능 : 애너테이션 옵션 추가
// 모든 필드의 Getter 메서드를 자동 생성합니다.
// getId(), getName() 등을 직접 안 짜도 됨
@Getter
// 빌더 패턴을 사용할 수 있게 해줍니다.
// Member.builder().id(1L).name("홍길동").build() 식의 가독성 좋은 객체 생성 가능
@Builder
// 모든 필드를 파라미터로 받는 생성자를 만듭니다.
// @Builder를 사용하기 위해 보통 필수로 붙입니다.
@AllArgsConstructor
// 파라미터가 없는 기본 생성자를 만듭니다.
// JPA(@Entity) 사용 시 필수 규칙을 지키기 위해 자주 사용
@NoArgsConstructor
// 객체를 출력할 때 내용을 예쁘게 보여주는 toString() 메서드 생성
@ToString
public class Board extends BaseEntity{
    
    // 엔티티 클래스는 실제 데이터 베이스의 테이블을 만드는 효과이므로,
    // 반드시 pk 를 생성해야 한다
    // 그래서 필수로 @Id 를 이용해서, pk 표시를 의무적으로 해야한다
    @Id
    // 마리아 디비에서 사용하는 기본 자동 생성 정책을 이용한다
    // 기본 키 값을 DB가 자동으로 생성하도록 위임
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long bno; // 실제 데이터베이스의 테이블의 컬럼1

    //추가 기능2 , 옵션 추가
    @Column(length = 500, nullable = false)
    private String title; // 실제 데이터베이스의 테이블의 컬럼2

    @Column(length = 2000, nullable = false)
    private String content; // 실제 데이터베이스의 테이블의 컬럼3

    @Column(length = 50, nullable = false)
    private String writer; // 실제 데이터베이스의 테이블의 컬럼4

    // 눈에 보이지 않지만, BaseEntity를 이용해서, regDate, modDate 도 추가가 될 예정.

    // 수정 메서드를 따로 만들기

    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }
}
