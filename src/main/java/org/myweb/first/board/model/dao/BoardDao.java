package org.myweb.first.board.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("boardDao")
public class BoardDao {

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	public ArrayList<Board> selectTop3(){
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectTop3");
		return (ArrayList<Board>)list;
	}
	
	public int selectListCount() {
		return sqlSessionTemplate.selectOne("boardMapper.selectListCount");
	}
	
	public ArrayList<Board> selectList(Paging paging){
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectList", paging);
		return (ArrayList<Board>)list;
	}
	
	public Board selectBoard(int boardNum) {
		return sqlSessionTemplate.selectOne("boardMapper.selectBoard", boardNum);
	}
	
	// dml --------------------------------------------------------	
	public void updateAddReadCount(int boardNum) {
		sqlSessionTemplate.update("boardMapper.updateAddReadCount", boardNum);
	}
	
	public int insertBoard(Board board) {
		return sqlSessionTemplate.insert("boardMapper.insertBoard", board);
	}
	
	public int insertReply(Board reply) {
		return sqlSessionTemplate.insert("boardMapper.insertReply", reply);
	}
	
	public int updateReplySeq(Board reply) {
		return sqlSessionTemplate.update("boardMapper.updateReplySeq", reply);
	}
	
	public int updateBoard(Board board) {
		return sqlSessionTemplate.update("boardMapper.updateBoard", board);
	}
	
	public int updateReply(Board reply) {
		return sqlSessionTemplate.update("boardMapper.updateReply", reply);
	}
	
	public int deleteBoard(Board board) {
		return sqlSessionTemplate.delete("boardMapper.deleteBoard", board);
	}
	
	// 검색 관련 ---------------------------------------
	public int selectSearchTitleCount(String keyword) {
		return sqlSessionTemplate.selectOne("boardMapper.selectSearchTitleCount", keyword);
	}
	
	public int selectSearchWriterCount(String keyword) {
		return sqlSessionTemplate.selectOne("boardMapper.selectSearchWriterCount", keyword);
	}
	
	public int selectSearchDateCount(Search search) {
		return sqlSessionTemplate.selectOne("boardMapper.selectSearchDateCount", search);
	}
	
	public ArrayList<Board> selectSearchTitle(Search search) {
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectSearchTitle", search);
		return (ArrayList<Board>)list;
	}
	
	public ArrayList<Board> selectSearchWriter(Search search) {
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectSearchWriter", search);
		return (ArrayList<Board>)list;
	}
	
	public ArrayList<Board> selectSearchDate(Search search) {
		List<Board> list = sqlSessionTemplate.selectList("boardMapper.selectSearchDate", search);
		return (ArrayList<Board>)list;
	}
}















