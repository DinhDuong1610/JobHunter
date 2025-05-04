package com.dinhduong.jobhunter.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinhduong.jobhunter.domain.Job;
import com.dinhduong.jobhunter.domain.response.job.ResCreateJobDTO;
import com.dinhduong.jobhunter.domain.response.job.ResUpdateJobDTO;
import com.dinhduong.jobhunter.service.JobService;
import com.dinhduong.jobhunter.util.annotation.ApiMessage;
import com.dinhduong.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.fetchJobById(job.getId());
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job id = " + job.getId() + " khong ton tai");
        }
        return ResponseEntity.ok().body(this.jobService.update(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.fetchJobById(id);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job id = " + id + " khong ton tai");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok().body(null);
    }

}
