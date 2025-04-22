package org.myweb.first.board.model.service;

import java.util.ArrayList;

import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;

public interface BoardService {
	ArrayList<Board> selectTop3();
	int selectListCount();
	ArrayList<Board> selectList(Paging paging);
	Board selectBoard(int boardNum);

	// dml --------------------
	void updateAddReadCount(int boardNum);
	int insertBoard(Board board);
	int insertReply(Board reply);
	int updateReplySeq(Board reply);
	int updateBoard(Board board);
	int updateReply(Board reply);
	int deleteBoard(Board board);

	// 검색 관련
	int selectSearchTitleCount(String keyword);
	int selectSearchWriterCount(String keyword);
	int selectSearchDateCount(Search search);
	ArrayList<Board> selectSearchTitle(Search search);
	ArrayList<Board> selectSearchWriter(Search search);
	ArrayList<Board> selectSearchDate(Search search);
}
