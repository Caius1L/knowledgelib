package com.lib.web.admin.main;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.lib.dto.FileInfoVO;
import com.lib.dto.JsonResult;
import com.lib.entity.Classification;
import com.lib.entity.FileInfo;
import com.lib.entity.KeywordInfo;
import com.lib.entity.UserInfo;
import com.lib.enums.Const;
import com.lib.service.admin.ClassificationService;
import com.lib.service.admin.KeywordService;
import com.lib.utils.StringValueUtil;

/**
 * 后台登录
 * 
 * @author l
 *
 */
@Controller
@RequestMapping("/admin")
public class ClassManageController {

	@Autowired
	private ClassificationService classService;
    @Autowired
    private KeywordService keywordService;
	/**
	 * 更新缩略图
	 * 
	 * @param files
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/update-classpic/{classId}", method = RequestMethod.POST)
	@ResponseBody
	public String uploadFile(@RequestParam("file") CommonsMultipartFile[] files, HttpSession session,
			@PathVariable("classId") Long classId) throws Exception {
		Classification classi = classService.findById(classId);
		String path = classi.getClassificationPicture();
		FileUtils.writeByteArrayToFile(new File(Const.ROOT_PATH + "thumbnail/" + path + ".png"), files[0].getBytes());
		return "success";
	}

	/**
	 * 修改节点
	 * 
	 * @param session
	 * @param c
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/update-classpic", method = RequestMethod.POST)
	public @ResponseBody Integer update(HttpSession session, Classification c) throws Exception {
		int res = classService.updatePicPath(c);
		return res;
	}
	/**
	 *获取所有关键词
	 * 
	 */
	@RequestMapping(value = "/keyword", method = RequestMethod.GET)
	public @ResponseBody List<KeywordInfo> selectall()throws Exception {
		return keywordService.selectall();
	}

	/**
	 * 删除节点
	 * 
	 * @param session
	 * @param classId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/del-classpic/{classId}", method = RequestMethod.POST)
	public @ResponseBody Integer del(HttpSession session, @PathVariable("classId") Long classId) throws Exception {
		int res = classService.delete(classId);
		return res;
	}

	/**
	 * 增加节点
	 * 
	 * @param session
	 * @param classId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/add-classpic", method = RequestMethod.POST)
	public @ResponseBody Integer add(HttpSession session, Classification c) throws Exception {
		c.setClassificationPicture(StringValueUtil.getUUID());
		int res = classService.insert(c);
		return res;
	}

	/**
	 * 分类管理主页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/class-manage-ui", method = RequestMethod.GET)
	public String ManageUI() {
		return "admin/class-manage";
	}

}
