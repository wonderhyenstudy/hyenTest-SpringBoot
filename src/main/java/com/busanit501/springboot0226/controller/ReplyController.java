package com.busanit501.springboot0226.controller;

import com.busanit501.springboot0226.dto.ReplyDTO;
import com.busanit501.springboot0226.service.ReplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// 이 클래스는 데이터를 반환하는 API 전용 컨트롤러다
@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {
    // ResponseEntity -> 반환 타입으로 사용할 예정.
    // 전달 1) 데이터 2) http 상태 코드 같이 전달.
    // 예시)_ 댓글 작성 , http 200 ok

    // 설명
    // 화면 -> 서버
    // 화면에서 댓글 작성 값 -> json 형식 전달 -> 서버 전달.->
    // ->json -> 일반클래스 변환(ReplyDTO)변환,

    // 서버 -> 화면 , 전달,
    //Map<String,Long> 으로전달 -> jackson 직렬화 -> 문자열로 변환,
    // json 문자열 형식으로 변환.

    // @RequestBody, JSON 의 문자열로 전달을 받아요.
    // 원래의 일반 클래스로 역직렬화,
    // Jackson 컨버터 도구가, 알아서, 역직렬화 해줌.

    // ReplyService 만들어서, 작업해야함.
    // 컨트롤러 클래스 상단에 작성하여 API 그룹의 이름을 지정합니다
    private final ReplyService replyService;


    @Tag(name = "hyen 테스트",
            description = "hyen 테스트 중입니다")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Long>> register(
            @Valid @RequestBody ReplyDTO replyDTO,
            // @RequestBody : 클라이언트가 보낸 HTTP 요청의 본문(Body)에 담긴 데이터를 자바 객체로 변환해주는 어노테이션
//            @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult
    ) throws BindException {
        log.info(" ReplyController replyDTO: " + replyDTO);
        // 확인용, 더미 데이터 ,

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // 실제 데이터베이스에 반영이 되는 서비스 기능으로 교체 작업.
        Long rno = replyService.register(replyDTO);
        Map<String,Long> resultMap = new HashMap<>();
        resultMap.put("rno",rno);

        return ResponseEntity.ok(resultMap);

//        Long rno = replyService.register(replyDTO);
//        Map<String,Long> map = Map.of("rno",rno);
//        return null;

//        Map<String,Long> map = Map.of("rno",123L);
        // ResponseEntity.ok : 200, 정상 응답 코드 의미,
        // map : 데이터를 같이 전달.
//        return ResponseEntity.ok(map);
    }
}
