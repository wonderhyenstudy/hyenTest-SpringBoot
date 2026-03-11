package com.busanit501.springboot0226.service;

import com.busanit501.springboot0226.dto.*;

public interface ReplyService {
    Long register(ReplyDTO replyDTO);

    //조회
    ReplyDTO read(Long rno);
    //수정
    void modify(ReplyDTO replyDTO);
    //삭제
    void remove(Long rno);

    // 게시글 하나에 대한 댓글 목록
    // 받는 타입 : PageRequestDTO
    // 반환 타입 : PageResonseDTO
    // 게시글(번호 알아야함) 하나에 대한 댓글 목록
    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);

}
