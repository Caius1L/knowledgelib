package com.lib.service.admin.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lib.dao.KeywordDao;
import com.lib.entity.KeywordInfo;
import com.lib.service.admin.KeywordService;
@Service
public class KeywordServiceImpl implements KeywordService {
	@Autowired
	private KeywordDao keywordDao;
	
	@Override
	public List<KeywordInfo> selectall() {
		return keywordDao.selectall();
	}
}
