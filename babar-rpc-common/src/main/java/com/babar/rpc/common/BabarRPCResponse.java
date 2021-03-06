package com.babar.rpc.common;

import java.io.Serializable;

public class BabarRPCResponse implements Serializable{
	private static final long serialVersionUID = 1L;

	private String requestId;
	private Throwable error;
	private Object result;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Throwable getError() {
		return error;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}
