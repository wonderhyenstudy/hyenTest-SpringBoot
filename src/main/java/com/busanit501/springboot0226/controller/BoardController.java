package com.busanit501.springboot0226.controller;

import com.busanit501.springboot0226.dto.*;
import com.busanit501.springboot0226.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    // 물리 저장소 경로를 불러오기.
    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    // 리스트 화면
    // src/main/resources/templates/board/list.html
    // http://localhost:8080/board/list
    // @GetMapping: 브라우저에서 '/board/list' 주소로 GET 방식 요청이 들어올 때 이 메서드가 실행됨
    // 데이터의 흐름: 사용자 요청 → PageRequestDTO → BoardService → PageResponseDTO → Model → HTML.
    /**
     * 게시판 목록을 페이징하여 조회합니다. (메서드 설명)
     *
     * @param pageRequestDTO 페이지 번호, 사이즈, 검색 조건이 담긴 객체
     * @param model 뷰(HTML)로 데이터를 전달하기 위한 객체
     * @return void (반환값 없음 - 요청 경로로 자동 이동)
     */
    /* JavaDoc 주석 : '문서화'를 위한 특수 주석
    @param: 매개변수(Parameter) 설명입니다. [변수명] [설명] 순으로 적습니다.
    @return: 메서드가 실행된 후 돌려주는 값(Return Value)에 대한 설명입니다.
    @throws (또는 @exception): 메서드 실행 중 발생할 수 있는 예외를 설명할 때 씁니다.
    @see: 참고할 다른 클래스나 메서드의 링크를 걸 때 씁니다.
    */
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
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        // 기존 목록에, 댓글 갯수 포함된 , 서비스 메서드로 교체 작업.
//        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);

        // 기존 목록에, 댓글 갯수 + 첨부 이미지가 모두 포함된 메서드로 교체 작업.
        PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);


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
    /**
     * 게시글 등록 처리를 수행합니다. (POST 방식)
     *
     * @param boardDTO 입력받은 게시글 데이터 (제목, 내용, 작성자 등)
     * @param bindingResult 유효성 검사 결과 (오류 여부 확인)
     * @param redirectAttributes 리다이렉트 시 데이터를 전달하기 위한 객체 (일회성 메시지)
     * @return String 등록 후 이동할 페이지 경로 (성능/목록 페이지로 리다이렉트)
     */
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
    /*
    @PostMapping("/remove")
    // 수정 화면에서, 삭제시 -> 히든으로 숨겨둔 페이지, 사이즈 정보를, page, size 전달을 하면
    // PageRequestDTO 가 자동으로 데이터를 맵핑을 함
    public String remove(Long bno, RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, remove 작업중");


        boardService.remove(bno);
        redirectAttributes.addAttribute("result", "removed");
        return "redirect:/board/list";
    }
    */

    @PostMapping("/remove")
    // 삭제시, 화면에서 넘겨받은 , 삭제할 이미지 파일을 받을 준비 : BoardDTO 를 이용함.
//    public String remove(Long bno,  RedirectAttributes redirectAttributes) {
    public String remove(BoardDTO boardDTO,  RedirectAttributes redirectAttributes) {
        log.info("BoardController 에서, remove 작업중");

        Long bno = boardDTO.getBno();


        // 실무에서, 삭제시, 먼저 DB 의 내용먼저 삭제 후, 그다음에, 물리파일 삭제 하기.
        // 이유는 만약) 작업중 오류가 발생했을 때, DB에서 삭제가 되어야 정상적으로 화면에서, 출력이 안됨.
        // 2) 만약, DB 삭제가 이루어지지 않고, 먼저 물리 파일만, 삭제가 된 상황이면,
        //  화면에서, 없는 파일을 계속 가리킵니다. 그러면, 출력시 오류가 발생 되거나, 또는 UX 가 안좋습니다.

        // 순서1
        // 데이터베이스 삭제하고,
        boardService.remove(bno);

        // 게시글에 첨부된 이미지 파일도 삭제.
        //추가
        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            // uploadController 가져와서 사용한다.
            removeFiles(fileNames);
        }

        redirectAttributes.addAttribute("result","removed");
        return "redirect:/board/list";
    }

    // 추가, 이미지 파일 삭제하는 함수
    // 물리서버 , 첨부 이미지 삭제 함수.
    public void removeFiles(List<String> fileNames) {
        for (String filename : fileNames) {
            Resource resource = new FileSystemResource(uploadPath+ File.separator+filename);
//            String resourceName = resource.getFilename();

            // 리턴 타입 Map 전달,
            Map<String,Boolean> resultMap = new HashMap<>();
            boolean deleteCheck = false;
            try {
                // 파일 삭제시, 이미지 파일일 경우, 원본 이미지와 , 썸네일 이미지 2개 있어서
                // 이미지 파일 인지 여부를 확인 후, 이미지 이면, 썸네일도 같이 제거해야함.
                String contentType = Files.probeContentType(resource.getFile().toPath());
                // 삭제 여부를 업데이트
                // 원본 파일을 제거하는 기능. (실제 물리 파일 삭제 )
                deleteCheck =resource.getFile().delete();

                if (contentType.startsWith("image")) {
                    // 썸네일 파일을 생성해서, 파일 클래스로 삭제를 진행.
                    // uploadPath : C:\\upload\springTest
                    // File.separator : C:\\upload\springTest\test1.jpg
                    File thumbFile = new File(uploadPath+ File.separator,"s_"+ filename);
                    // 실제 물리 파일 삭제
                    thumbFile.delete();
                }
            }
            catch (Exception e) {
                log.error(e.getMessage());
            }
            resultMap.put("result", deleteCheck);
//            return resultMap;
        }
    }

}