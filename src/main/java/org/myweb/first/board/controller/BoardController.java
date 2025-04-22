package org.myweb.first.board.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.myweb.first.board.model.dto.Board;
import org.myweb.first.board.model.service.BoardService;
import org.myweb.first.common.FileNameChange;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.notice.model.dto.Notice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BoardController {
	// 로그 객체 생성
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

	@Autowired
	private BoardService boardService;

	// 뷰 페이지 내보내기용 메소드 ---------------------------------------
	// 새 게시글 원글 등록 페이지 이동 처리용
	@RequestMapping("bwform.do")
	public String moveWritePage() {
		return "board/boardWriteForm";
	}

	// 댓글, 대댓글 등록 페이지로 이동 처리용
	@RequestMapping("breplyform.do")
	public ModelAndView moveReplyPage(ModelAndView mv, @RequestParam("bnum") int boardNum,
			@RequestParam("page") int currentPage) {

		mv.addObject("bnum", boardNum);
		mv.addObject("currentPage", currentPage);
		mv.setViewName("board/boardReplyForm");

		return mv;
	}

	// 게시글 (원글, 댓글, 대댓글) 수정 페이지로 이동 처리용
	@RequestMapping("bupview.do")
	public String moveBoardUpdatePage(Model model, @RequestParam("bnum") int boardNum,
			@RequestParam("page") int currentPage) {

		// 수정 페이지로 전달할 board 정보 조회함
		Board board = boardService.selectBoard(boardNum);

		if (board != null) {
			model.addAttribute("board", board);
			model.addAttribute("currentPage", currentPage);
			return "board/boardUpdateView";
		} else {
			model.addAttribute("message", boardNum + "번 게시글 수정페이지로 이동 실패!");
			return "common/error";
		}
	}

	// 요청 처리용 메소드 -----------------------------------------------------

	@RequestMapping(value = "btop3.do", method = RequestMethod.POST, produces = "application/json; charset:UTF-8")
	@ResponseBody
	// public String boardCountTop3Method() { //JSONObject 클래스 사용시 리턴 방식
	public Map<String, Object> boardCountTop3Method() { // Jackson library 사용 리턴 방식
		logger.info("btop3.do run...");

		// 서비스 모델로 메소드 실행 요청하고 결과 받기
		ArrayList<Board> list = boardService.selectTop3();
		logger.info("btop3.do list : " + list);

//		JSONArray jarr = new JSONArray();
//		
//		for(Board board : list) {
//			JSONObject job = new JSONObject();
//			job.put("bnum"	, board.getBoardNum());
//			job.put("btitle", board.getBoardTitle());
//			job.put("rcount", board.getBoardReadCount());
//			
//			jarr.add(job);
//		}
//		
//		JSONObject sendJson = new JSONObject();
//		sendJson.put("blist", jarr);
//		
//		return sendJson.toJSONString();	

		// Jackson 라이브러리 사용 ---------------------------------------------
		Map<String, Object> top3Result = new HashMap<>();
		top3Result.put("blist", list);

		return top3Result; // Jackson이 자동으로 JSON으로 변환
	}

	// 게시글 전체 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
	@RequestMapping("blist.do")
	public ModelAndView boardListMethod(ModelAndView mv, @RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {
		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 목록 갯수 기본 10개로 지정함
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 총 목록 갯수 조회해서, 총 페이지 수 계산함
		int listCount = boardService.selectListCount();
		// 페이지 관련 항목들 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "blist.do");
		paging.calculate();

		// 서비스 모델로 페이징 적용된 목록 조회 요청하고 결과받기
		ArrayList<Board> list = boardService.selectList(paging);

		if (list != null && list.size() > 0) { // 조회 성공시
			// ModelAndView : Model + View
			mv.addObject("list", list); // request.setAttribute("list", list) 와 같음
			mv.addObject("paging", paging);

			mv.setViewName("board/boardListView");
		} else { // 조회 실패시
			mv.addObject("message", currentPage + "페이지에 출력할 게시글 목록 조회 실패!");
			mv.setViewName("common/error");
		}

		return mv;
	}

	// 게시글 (원글, 댓글, 대댓글) 상세 내용보기 요청 처리용
	@RequestMapping("bdetail.do")
	public ModelAndView boardDetailMethod(@RequestParam("bnum") int boardNum,
			@RequestParam(name = "page", required = false) String page, ModelAndView mv) {
		logger.info("bdetail.do : " + boardNum);

		int currentPage = 1; // 상세보기 페이지에서 목록 버튼 누르면, 보고있던 목록 페이지로 돌아가기 위해 저장함
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		Board board = boardService.selectBoard(boardNum);

		// 조회수 1증가 처리
		boardService.updateAddReadCount(boardNum);

		if (board != null) {
			mv.addObject("board", board);
			mv.addObject("currentPage", currentPage);
			mv.setViewName("board/boardDetailView");
		} else {
			mv.addObject("message", boardNum + "번 게시글 상세보기 요청 실패!");
			mv.setViewName("common/error");
		}

		return mv;
	}

	// 첨부파일 다운로드 요청 처리용 메소드
	// 스프링에서는 파일 다운로드는 스프링이 제공하는 View 클래스를 상속받은 클래스를 사용하도록 정해 놓았음
	// => 공통모듈로 파일다운로드용 뷰 클래스를 따로 만듦 => 뷰리졸버에서 연결 처리함
	// => 리턴타입은 반드시 ModelAndView 여야 함
	@RequestMapping("bfdown.do")
	public ModelAndView fileDownMethod(ModelAndView mv, HttpServletRequest request,
			@RequestParam("ofile") String originalFileName, @RequestParam("rfile") String renameFileName) {

		// 게시글 첨부파일 저장 폴더 경로 지정
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");
		// 저장 폴더에서 읽을 파일에 대한 File 객체 생성
		File downFile = new File(savePath + "\\" + renameFileName);
		// 파일 다운시 브라우저로 내보낼 원래 파일에 대한 File 객체 생성
		File originFile = new File(originalFileName);

		// 파일 다운 처리용 뷰클래스 id명과 다운로드할 File 객체를 ModelAndView 에 담아서 리턴함
		mv.setViewName("filedown"); // 뷰클래스의 id명 기입
		mv.addObject("originFile", originFile);
		mv.addObject("renameFile", downFile);

		return mv;
	}

	// 새 게시글 원글 등록 요청 처리용 (파일 업로드 기능 포함)
	@RequestMapping(value = "binsert.do", method = RequestMethod.POST)
	public String boardInsertMethod(Board board, @RequestParam(name = "ofile", required = false) MultipartFile mfile,
			HttpServletRequest request, Model model) {
		logger.info("binsert.do : " + board);

		// 게시 원글 첨부파일 저장 폴더를 경로 저장
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");

		// 첨부파일이 있을 때
		if (!mfile.isEmpty()) {
			// 전송온 파일이름 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;

			// 저장 폴더에는 변경된 파일이름을 파일을 저장 처리함
			// 바꿀 파일명 : 년월일시분초.확장자
			if (fileName != null && fileName.length() > 0) {
				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
				logger.info("변경된 첨부 파일명 확인 : " + renameFileName);

				try {
					// 저장 폴더에 바뀐 파일명으로 파일 저장하기
					mfile.transferTo(new File(savePath + "\\" + renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("message", "첨부파일 저장 실패!");
					return "common/error";
				}
			} // 파일명 바꾸어 저장하기

			// board 객체에 첨부파일 정보 저장하기
			board.setBoardOriginalFileName(fileName);
			board.setBoardRenameFileName(renameFileName);
		} // 첨부파일 있을 때

		if (boardService.insertBoard(board) > 0) {
			// 새 게시 원글 등록 성공시, 게시 목록 페이지로 이동 처리
			return "redirect:blist.do";
		} else {
			model.addAttribute("message", "새 게시 원글 등록 실패!");
			return "common/error";
		}

	} // binsert.do closed

	// 게시글 댓글, 대댓글 등록 요청 처리용 (파일 업로드 기능 없음)
	@RequestMapping(value = "breply.do", method = RequestMethod.POST)
	public String replyInsertMethod(Board reply, Model model, @RequestParam("bnum") int bnum,
			@RequestParam("page") int page) {
		logger.info("breply.do : " + reply);

		// 1. 새로 등록할 댓글의 원글 | 대댓글의 댓글 을 조회해 옴
		Board origin = boardService.selectBoard(bnum);

		// 2. 새로 등록할 댓글 | 대댓글의 레벨을 지정함
		reply.setBoardLev(origin.getBoardLev() + 1);

		// 3. 참조원글번호(boardRef) 지정함
		reply.setBoardRef(origin.getBoardRef());

		// 4. 새로 등록할 글이 대댓글이면 (boardLev == 3), 참조 댓글번호(boardReplyRef) 지정함
		if (reply.getBoardLev() == 3) {
			reply.setBoardReplyRef(origin.getBoardReplyRef());
		}

		// 5. 최근 등록 댓글 } 대댓글의 순번을 1로 지정함
		reply.setBoardReplySeq(1);
		// 6. 기존 같은 레벨 & 같은 원글 | 댓글에 기록된 글은 순번(boardReplySeq)을 1증가 처리함
		boardService.updateReplySeq(reply);

		if (boardService.insertReply(reply) > 0) {
			// 게시글 댓글 | 대댓글 등록 성공시, 게시 목록 페이지로 이동 처리
			return "redirect:blist.do?page=" + page;
		} else {
			model.addAttribute("message", bnum + "번 글에 대한 댓글 | 대댓글 등록 실패!");
			return "common/error";
		}

	} // breply.do closed

	// 게시 원글 수정 요청 처리용 (첨부파일 수정 기능 포함)
	@RequestMapping(value = "boriginupdate.do", method = RequestMethod.POST)
	public String boardUpdateMethod(Board board, Model model, HttpServletRequest request,
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "deleteFlag", required = false) String delFlag,
			@RequestParam(name = "ofile", required = false) MultipartFile mfile) {
		logger.info("boriginupdate.do : " + board);

		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 첨부파일 관련 변경 사항 처리
		// 공지사항 첨부파일 저장 폴더 경로 지정
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");

		// 1. 원래 첨부파일이 있는데, '파일삭제'를 체크한 경우
		// 또는 원래 첨부파일이 있는데 새로운 첨부파일로 변경 업로드된 경우
		// => 이전 파일 정보 삭제함
		if (board.getBoardOriginalFileName() != null
				&& ((delFlag != null && delFlag.equals("yes")) || (!mfile.isEmpty()))) {
			// 저장 폴더에서 이전 파일은 삭제함
			new File(savePath + "\\" + board.getBoardRenameFileName()).delete();
			// board 안의 파일 정보도 삭제함
			board.setBoardOriginalFileName(null);
			board.setBoardRenameFileName(null);
		}

		// 2. 첨부파일이 있을 때 (변경 또는 추가)
		if (!mfile.isEmpty()) {
			// 전송온 파일이름 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;

			// 저장 폴더에는 변경된 파일이름을 파일을 저장 처리함
			// 바꿀 파일명 : 년월일시분초.확장자
			if (fileName != null && fileName.length() > 0) {
				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
				logger.info("변경된 첨부 파일명 확인 : " + renameFileName);

				try {
					// 저장 폴더에 바뀐 파일명으로 파일 저장하기
					mfile.transferTo(new File(savePath + "\\" + renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("message", "첨부파일 저장 실패!");
					return "common/error";
				}
			} // 파일명 바꾸어 저장하기

			// board 객체에 첨부파일 정보 저장하기
			board.setBoardOriginalFileName(fileName);
			board.setBoardRenameFileName(renameFileName);
		} // 첨부파일 있을 때

		if (boardService.updateBoard(board) > 0) {
			// 게시 원글 수정 성공시, 상세보기 페이지로 이동 처리
			return "redirect:bdetail.do?bnum=" + board.getBoardNum() + "&page=" + currentPage;
		} else {
			model.addAttribute("message", board.getBoardNum() + "번 게시 원글 수정 실패!");
			return "common/error";
		}
	}

	// 게시글 댓글, 대댓글 수정 요청 처리용
	@RequestMapping(value = "breplyupdate.do", method = RequestMethod.POST)
	public String replyUpdateMethod(Board reply, Model model, @RequestParam("page") int currentPage) {
		logger.info("breplyupdate.do : " + reply);

		if (boardService.updateReply(reply) > 0) {
			// 댓글, 대댓글 수정 성공시, 상세보기 페이지로 이동 처리
			return "redirect:bdetail.do?bnum=" + reply.getBoardNum() + "&page=" + currentPage;
		} else {
			model.addAttribute("message", reply.getBoardNum() + "번 댓글 또는 대댓글 수정 실패!");
			return "common/error";
		}
	}

	// 게시글 (원글, 댓글, 대댓글) 삭제 요청 처리용
	@RequestMapping("bdelete.do")
	public String boardDeleteMethod(Board board, Model model, HttpServletRequest request) {
		if (boardService.deleteBoard(board) > 0) {
			// 게시글 삭제 성공시 원글인 경우, 첨부파일도 같이 삭제 처리함
			if (board.getBoardLev() == 1) {
				if (board.getBoardRenameFileName() != null && board.getBoardRenameFileName().length() > 0) {
					// 게시글 첨부파일 저장 폴더 지정
					String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");
					// 저장 폴더에서 파일 삭제 처리함
					new File(savePath + "\\" + board.getBoardRenameFileName()).delete();
				}
			}

			return "redirect:blist.do";
		} else {
			model.addAttribute("message", board.getBoardNum() + "번 게시글 삭제 실패!");
			return "common/error";
		}
	}

	// 게시글 검색 관련 **********************************************

	// 게시글 제목 검색 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
	@RequestMapping("bsearchTitle.do")
	public ModelAndView boardSearchTitleMethod(
			ModelAndView mv, 
			@RequestParam("action") String action,
			@RequestParam("keyword") String keyword, 
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {
		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 목록 갯수 기본 10개로 지정함
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 검색결과가 적용된 총 목록 갯수 조회해서, 총 페이지 수 계산함
		int listCount = boardService.selectSearchTitleCount(keyword);
		// 페이지 관련 항목들 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchTitle.do");
		paging.calculate();

		// 마이바티스 매퍼에서 사용되는 메소드는 Object 1개만 전달할 수 있음
		// paging.startRow, paging.endRow, keyword 같이 전달해야 하므로 => 객체 하나를 만들어서 저장해서 보냄
		Search search = new Search();
		search.setKeyword(keyword);
		search.setStartRow(paging.getStartRow());
		search.setEndRow(paging.getEndRow());

		// 서비스 모델로 페이징 적용된 목록 조회 요청하고 결과받기
		ArrayList<Board> list = boardService.selectSearchTitle(search);

		if (list != null && list.size() > 0) { // 조회 성공시
			// ModelAndView : Model + View
			mv.addObject("list", list); // request.setAttribute("list", list) 와 같음
			mv.addObject("paging", paging);
			mv.addObject("action", action);
			mv.addObject("keyword", keyword);

			mv.setViewName("board/boardListView");
		} else { // 조회 실패시
			mv.addObject("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
			mv.setViewName("common/error");
		}

		return mv;
	}

	// 게시글 작성자 검색 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
	@RequestMapping("bsearchWriter.do")
	public ModelAndView boardSearchWriterMethod(
			ModelAndView mv, 
			@RequestParam("action") String action,
			@RequestParam("keyword") 
			String keyword, @RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {
		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 목록 갯수 기본 10개로 지정함
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 검색결과가 적용된 총 목록 갯수 조회해서, 총 페이지 수 계산함
		int listCount = boardService.selectSearchWriterCount(keyword);
		// 페이지 관련 항목들 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchWriter.do");
		paging.calculate();

		// 마이바티스 매퍼에서 사용되는 메소드는 Object 1개만 전달할 수 있음
		// paging.startRow, paging.endRow, keyword 같이 전달해야 하므로 => 객체 하나를 만들어서 저장해서 보냄
		Search search = new Search();
		search.setKeyword(keyword);
		search.setStartRow(paging.getStartRow());
		search.setEndRow(paging.getEndRow());

		// 서비스 모델로 페이징 적용된 목록 조회 요청하고 결과받기
		ArrayList<Board> list = boardService.selectSearchWriter(search);

		if (list != null && list.size() > 0) { // 조회 성공시
			// ModelAndView : Model + View
			mv.addObject("list", list); // request.setAttribute("list", list) 와 같음
			mv.addObject("paging", paging);
			mv.addObject("action", action);
			mv.addObject("keyword", keyword);

			mv.setViewName("board/boardListView");
		} else { // 조회 실패시
			mv.addObject("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
			mv.setViewName("common/error");
		}

		return mv;
	}

	// 게시글 등록날짜 검색 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
	@RequestMapping("bsearchDate.do")
	public ModelAndView boardSearchDateMethod(
			ModelAndView mv, Search search, 
			@RequestParam("action") String action,
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {
		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 목록 갯수 기본 10개로 지정함
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 검색결과가 적용된 총 목록 갯수 조회해서, 총 페이지 수 계산함
		int listCount = boardService.selectSearchDateCount(search);
		// 페이지 관련 항목들 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchDate.do");
		paging.calculate();

		// 마이바티스 매퍼에서 사용되는 메소드는 Object 1개만 전달할 수 있음
		// paging.startRow, paging.endRow, begin, end 같이 전달해야 하므로 => 객체 하나를 만들어서 저장해서 보냄
		search.setStartRow(paging.getStartRow());
		search.setEndRow(paging.getEndRow());

		// 서비스 모델로 페이징 적용된 목록 조회 요청하고 결과받기
		ArrayList<Board> list = boardService.selectSearchDate(search);

		if (list != null && list.size() > 0) { // 조회 성공시
			// ModelAndView : Model + View
			mv.addObject("list", list); // request.setAttribute("list", list) 와 같음
			mv.addObject("paging", paging);
			mv.addObject("action", action);
			mv.addObject("begin", search.getBegin());
			mv.addObject("end", search.getEnd());

			mv.setViewName("board/boardListView");
		} else { // 조회 실패시
			mv.addObject("message", action + "에 대한 " + search.getBegin() + "부터 " + search.getEnd()
					+ "기간 사이에 등록한 공지글 검색 결과가 존재하지 않습니다.");
			mv.setViewName("common/error");
		}

		return mv;
	}

}
