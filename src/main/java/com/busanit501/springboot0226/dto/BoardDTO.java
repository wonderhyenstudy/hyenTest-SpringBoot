package com.busanit501.springboot0226.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Getter, Setter, toString 등을 한방에 다 만들어줌.
@Data
// 빌더 패턴을 사용할 수 있게 해줍니다.
// Member.builder().id(1L).name("홍길동").build() 식의 가독성 좋은 객체 생성 가능
@Builder
// 모든 필드를 파라미터로 받는 생성자를 만듭니다.
// @Builder를 사용하기 위해 보통 필수로 붙입니다.
@AllArgsConstructor
// 파라미터가 없는 기본 생성자를 만듭니다.
// JPA(@Entity) 사용 시 필수 규칙을 지키기 위해 자주 사용
@NoArgsConstructor
public class BoardDTO {
    private Long bno;
    @NotEmpty
    @Size(min = 3, max = 100) // 추후 유효성 검사를 하며. 이 값으로 검사를 한다
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String writer;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
