package com.busanit501.springboot0226.controller;

import com.busanit501.springboot0226.dto.BoardDTO;
import com.busanit501.springboot0226.dto.PageRequestDTO;
import com.busanit501.springboot0226.dto.PageResponseDTO;
import com.busanit501.springboot0226.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 이 클래스가 Spring MVC의 컨트롤러임을 선언
@Controller
// 공통 경로. 해당 컨트롤러의 공통 URL 경로를 지정.
@RequestMapping("/board")
// log 변수 사용 가능. 롬복 어노테이션. 로그 출력 객체를 자동으로 생성.
@Log4j2
// MemberService 자동 주입. final로 선언된 **서비스(Service) 객체를 자동으로 주입(DI)**해 줍니다
@RequiredArgsConstructor
public class BoardController {
    // 의존성 주입 대상 (final 필수)
    private final BoardService boardService;


    // 리스트 화면
    // src/main/resources/templates/board/list.html
    // http://localhost:8080/board/list
    // @GetMapping: 브라우저에서 '/board/list' 주소로 GET 방식 요청이 들어올 때 이 메서드가 실행됨
    // 데이터의 흐름: 사용자 요청 → PageRequestDTO → BoardService → PageResponseDTO → Model → HTML.
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        /**
         * [파라미터 설명]
         * 1. PageRequestDTO: 사용자가 보낸 page(페이지 번호), size(한 페이지당 개수),
         *    type(검색 조건), keyword(검색어) 등을 스프링이 자동으로 객체로 변환해 담아줍니다.
         * 2. Model: 컨트롤러에서 가공한 데이터를 뷰(HTML)로 넘겨주기 위해 사용하는 바구니 객체입니다.
         */

        // 1. 서비스 계층의 list 메서드를 호출하여 결과 데이터를 가져옵니다.
        // boardService.list()는 DB에서 게시글 목록을 가져오고, 페이징 계산(시작/끝 페이지 등)까지 완료한
        // PageResponseDTO 객체를 반환합니다.
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        // 2. @Log4j2를 사용하여 콘솔에 로그를 출력합니다.
        // 서비스로부터 받은 데이터(목록, 전체 개수, 현재 페이지 등)가 제대로 넘어왔는지 확인하는 디버깅 용도입니다.
        log.info("BoardController 에서, responseDTO 확인" + responseDTO);

        // 3. 뷰(HTML)에서 사용할 수 있도록 데이터를 모델에 담습니다.
        // 첫 번째 인자 "responseDTO"는 HTML 파일에서 사용할 변수 이름입니다.
        // 타임리프 파일에서 ${responseDTO.dtoList} 처럼 접근하여 목록을 출력하게 됩니다.
        model.addAttribute("responseDTO", responseDTO);

        /**
         * [반환형이 void인 이유]
         * 스프링의 규칙에 따라 리턴 타입이 void이면 매핑 주소인 "/board/list"와 동일한 경로인
         * 'src/main/resources/templates/board/list.html' 파일을 자동으로 찾아 렌더링합니다.
         */

    }

    // 글쓰기 화면
    // 화면 제공
    @GetMapping("/register")
    public void registerGet(){

    }
    // 화면 로직
    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, registerPost 작업중");

        // 서버에서 유효성 체크를 했을 경우
        if(bindingResult.hasErrors()) {
            log.info("BoardController 에서, registerPost , 유효성 오류 발생. ");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        // 유효성 체크를 통과한 경우.
        log.info("boardDTO 확인 : " + boardDTO);
        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/list";

    }


    // 화면 하나 읽기. 상세화면
    @GetMapping({"/read", "/modify"})
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("BoardController 에서, read , boardDTO 확인 : " +boardDTO );
        model.addAttribute("dto",boardDTO);
    }

    // 수정하기
    // 화면 로직
    @PostMapping("/modify")
    public String modify(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                         PageRequestDTO pageRequestDTO,
                         RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, modify 작업중");

        // 서버에서 유효성 체크를 했을 경우
        if(bindingResult.hasErrors()) {
            log.info("BoardController 에서, modify , 유효성 오류 발생. ");
            String link = pageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno",boardDTO.getBno());
            return "redirect:/board/modify?"+link;
        }
        // 유효성 체크를 통과한 경우.
        log.info("boardDTO 확인 : " + boardDTO);
        boardService.modify(boardDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        return "redirect:/board/read";

    }

    // 삭제
    @PostMapping("/remove")
    // 수정 화면에서, 삭제시 -> 히든으로 숨겨둔 페이지, 사이즈 정보를, page, size 전달을 하면
    // PageRequestDTO 가 자동으로 데이터를 맵핑을 함
    public String remove(Long bno, RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, remove 작업중");

        boardService.remove(bno);
        redirectAttributes.addAttribute("result", "removed");
        return "redirect:/board/list";
    }




}
