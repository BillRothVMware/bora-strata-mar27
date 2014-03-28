package com.vmware.loginsight.send;

public enum LogUploadClientReturnStatus {
	FAIL(-1),
	SUCCESS(1);
	
	int value;
	private LogUploadClientReturnStatus(int value) {
		this.value = value;
	}
	
}