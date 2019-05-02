package net.Ildar.DatabaseSync;

import java.util.Objects;

/**
 * The base class for database records
 */
public class Job {
    /**
     * department code
     */
    private String depCode;
    /**
     * job name
     */
    private String depJob;
    /**
     * the job description
     */
    private String description;

    Job(String depCode, String depJob, String description) {
        this.depCode = trimParam(depCode, 20);
        this.depJob = trimParam(depJob, 100);
        this.description = trimParam(description, 255);
    }

    private String trimParam(String param, int maxLength) {
        if (param == null)
            return null;
        param = param.trim();
        if (param.length() > maxLength)
            param = param.substring(0, maxLength);
        return param;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDepJob() {
        return depJob;
    }

    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Job))
            return false;
        Job job = (Job) obj;
        return Objects.equals(depCode, job.depCode) && Objects.equals(depJob, job.depJob);
    }

    @Override
    public int hashCode() {
        return (depCode + ":" + depJob).hashCode();
    }
}