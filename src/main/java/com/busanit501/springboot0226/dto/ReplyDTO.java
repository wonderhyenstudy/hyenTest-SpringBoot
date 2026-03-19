package com.busanit501.springboot0226.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDTO {
    // 댓글의 구분 번호,
    private Long rno;

    // 부모의 게시글 번호,
    @NotNull
    private Long bno;

    @NotEmpty
    private String replyText;

    @NotEmpty
    private String replyer;
    
    // 댓글이 작성 시간
    // 화면으로 부터 시간을 전달 받을 경우, 위의 정의한 포맷 형식으로 변경
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    // 웹브라우저로 데이터를 보낼 때, 이 항목은 숨기기
    @JsonIgnore
    private LocalDateTime modDate;

}