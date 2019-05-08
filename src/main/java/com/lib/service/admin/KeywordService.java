package com.lib.service.admin;

import java.util.List;

import com.lib.entity.KeywordInfo;

public interface KeywordService {
   /**
    * 获取所有关键字
    */
	List<KeywordInfo> selectall();
}
