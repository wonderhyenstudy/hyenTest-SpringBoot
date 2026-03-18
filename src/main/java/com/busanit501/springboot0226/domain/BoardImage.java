package com.busanit501.springboot0226.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board") // BoardImage 조회시 Board 조회를 안함. 제외함
// Comparable 비교를 해서 -> 정렬을 하는게 목적. 객체의 기본 정렬 기준 정의
public class BoardImage implements Comparable<BoardImage>{

    @Id
    private String uuid;

    private String fileName;

    private int ord;

    // 연관관계 설정 1번,
    // Many : BoardImage , one : board
    @ManyToOne
    private Board board;

    // implements 메서드 구현 필수!
    public int compareTo(BoardImage other) {
        // 결과가 음수 : 앞으로 배치
        // 결과가 양수 : 뒤로 배치
        return this.ord - other.ord;
    }

    // 엔티티 클래스에서는, setter 만들지 않는다. 이유? 불변성을 유지하기 위해서
    // 지정된 작업외에는 변경이 안되도록 안전장치
    // 따로 메서드를 만들어서, 수동으로 Board 객체를 변경
    public void changeBoard(Board board) {
        this.board = board;
    }

}
