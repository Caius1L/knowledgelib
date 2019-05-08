package com.lib.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lib.dto.FileInfoVO;
import com.lib.entity.Classification;
import com.lib.entity.FileInfo;

/**
 * 文件dao操作
 * 
 * @author l
 *
 */
public interface FileInfoDao {
	/**
	 * 插入一条文件信息
	 * 
	 * @param record
	 * @return
	 */
	int insert(FileInfo record);
	
	/**
	 * 判断是否有相同的文本
	 * 
	 * @param md5
	 * @return
	 */
	int issame(String md5);

	/**
	 * 根据主键查询一个文件信息
	 * 
	 * @param fileId
	 * @return
	 */
	FileInfo selectByPrimaryKey(Long fileId);

	/**
	 * 更新一个文件信息
	 * 
	 * @param record
	 * @return
	 */
	int updateByPrimaryKey(FileInfo record);

	/**
	 * 删除一个文件信息
	 * 
	 * @param fileId
	 * @return
	 */
	int deleteByPrimaryKey(Long fileId);

	/**
	 * 得到为处理过的文件
	 * 
	 * @param i
	 * @return
	 */
	List<FileInfo> getFilesByState(Integer fileState);

	/**
	 * 通过uuid得到一个文件
	 * 
	 * @param uuid
	 * @return
	 */
	List<FileInfoVO> getFileInfoByUuid(String fileUuid);

	/**
	 * 通过fileId得到一个文件
	 * 
	 * @param uuid
	 * @return
	 */
	FileInfo getFileInfoByFileId(Long fileId);
	
	/**
	 * 设置文件的状态
	 * 
	 * @param uuid
	 * @param i
	 */
	void setFileStateByUuid(@Param("fileUuid") String uuid, @Param("fileState") Integer i);

	/**
	 * 修改文件后缀名
	 * 
	 * @param fileId
	 * @param fileExe
	 */
	void modifyFileExeById(@Param("fileId") Long fileId, @Param("fileExt") String fileExt);

	/**
	 * 得到为处理过的文件
	 * 
	 * @param searchValue
	 * 
	 * @param i
	 * @return
	 */
	List<FileInfoVO> getFilesByUserId(@Param("userId") Long userId, @Param("searchValue") String searchValue);

	/**
	 * 得到一个子节点所有的共有文件
	 * 
	 * @param searchValue
	 * 
	 * @param i
	 * @return
	 */
	List<FileInfoVO> getAllChildFiles(List<Classification> ids);

	/**
	 * 修改文件的基本信息
	 * 
	 * @param fileInfo
	 * @return
	 */
	int updateByUuid(FileInfo fileInfo);

	/**
	 * 查询一个用户可用的文档
	 * 
	 * @param searchInfo
	 * @param userId
	 * @return
	 */
	List<FileInfo> searchFileInfoByNameOrId(@Param("searchInfo") String searchInfo, @Param("userId") Long userId);

	/**
	 * 得到所有文档
	 * 
	 * @param string
	 * @return
	 */
	List<FileInfoVO> getFiles(String searchValue);

	/**
	 * 插入统计数据
	 * 
	 * @param userId
	 * @param fileId
	 */
	int insertClickInfo(@Param("userId") Long userId, @Param("fileId") Long fileId);

	/**
	 * 得到用户浏览历史
	 * 
	 * @param userId
	 * @return
	 */
	List<FileInfoVO> getRecentFileInfoByUserId(Long userId);

	/**
	 * 得到最近记录
	 * 
	 * @return
	 */
	List<FileInfoVO> getRecentShare();

	/**
	 * 获得近期点击率最高的文档
	 * 
	 * @param date
	 * @param date2
	 * @return
	 */
	List<FileInfoVO> getRecentHotFile();

}
