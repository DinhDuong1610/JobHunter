package com.dinhduong.jobhunter.domain.response.resume;

import java.time.Instant;

import com.dinhduong.jobhunter.util.constant.ResumeStateEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ResUpdateResumeDTO {
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    private String createdBy;

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
