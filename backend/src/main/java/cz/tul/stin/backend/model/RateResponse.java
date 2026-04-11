package cz.tul.stin.backend.model;

import javax.annotation.processing.Generated;

public abstract class RateResponse {
    private boolean success;
    private String source;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
