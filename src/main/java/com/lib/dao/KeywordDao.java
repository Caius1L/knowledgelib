package com.lib.dao;

import java.util.List;

import com.lib.entity.KeywordInfo;

public interface KeywordDao {
    /**
     * 取出所有的关键字
     */
	List<KeywordInfo> selectall();
}
