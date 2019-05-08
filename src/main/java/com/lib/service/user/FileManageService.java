package com.lib.service.user;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.lib.dto.FileInfoVO;
import com.lib.entity.Classification;

/**
 * 用户管理
 * 
 * @author Yu Yufeng
 *
 */
public interface FileManageService {

	/**
	 * 所有子节点的文件
	 * 
	 * @param pageNo
	 * @param classId
	 * @return
	 */
	PageInfo<FileInfoVO> getAllChildFiles(int pageNo, Long classId);

	/**
	 * 得到某一用户上传的文件分页
	 * 
	 * @param userId
	 * @param searchValue
	 * @return
	 */
	PageInfo<FileInfoVO> getFileInfoPageByUserId(int pageNo, Long userId, String order, String searchValue);

	/**
	 * 得到某一类别的子类别
	 * 
	 * @param parentId
	 * @return
	 */
	List<Classification> getClassificationByParentId(Long parentId);

	/**
	 * 得到某一类别信息
	 * 
	 * @param fileClassId
	 * @return
	 */
	Classification getClassificationById(Long fileClassId);

	/**
	 * 得到父节点链表
	 * 
	 * @param fileClassId
	 * @return
	 */
	List<Classification> getFatherClassesById(Long fileClassId);

	/**
	 * 得到一个类别的所有子节点
	 * 
	 * @param fileClassId
	 * @return
	 */
	List<Classification> getAllChildClassesById(Long fileClassId);

	/**
	 * 得到所有文件
	 * 
	 * @param pageNo
	 * @param string
	 * @param searchValue
	 * @return
	 */
	PageInfo<FileInfoVO> getFileInfoPage(Integer pageNo, String string, String searchValue);

	/**
	 * 得到用户最近的浏览记录
	 * @param userId
	 * @return
	 */
	List<FileInfoVO> getRecenREeadtFile(Long userId);

	/**
	 * 得到最近分享
	 * @param userId
	 * @return
	 */
	List<FileInfoVO> getRecenShareFile(Long userId);

	/**
	 * 获得最近1个月热门文档前7
	 * @return
	 */
	List<FileInfoVO> getRecentHotFile();

}
