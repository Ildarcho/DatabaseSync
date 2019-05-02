package net.Ildar.DatabaseSync;

import java.util.HashSet;

/**
 * Custom implementation of HashSet to contain the set of jobs
 */
public class JobSet extends HashSet<Job> {

    /**
     * gets description for given job, if current set contains it
     *
     * @param job Job object
     * @return description
     */
    public String getDescription(Job job) {
        for (Job j : this) {
            if (j.equals(job))
                return j.getDescription();
        }
        return null;
    }
}
