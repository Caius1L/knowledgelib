package com.lib.entity;

public class KeywordInfo {
    private Long keyId;
    private String keyWord;
	public Long getKeyId() {
		return keyId;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	@Override
	public String toString() {
		return "KeywordInfo [keyId=" + keyId + ", keyWord=" + keyWord+ "]";
	}
}
