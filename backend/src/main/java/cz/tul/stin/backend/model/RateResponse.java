package cz.tul.stin.backend.model;

import javax.annotation.processing.Generated;

public abstract class RateResponse {
    private boolean success;
    private String base;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
