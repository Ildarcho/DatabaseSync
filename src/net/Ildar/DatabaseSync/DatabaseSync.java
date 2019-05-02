package net.Ildar.DatabaseSync;


import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Performs database synchronization with xml file
 */
public class DatabaseSync {
    /**
     * Log4j logger
     */
    private Logger logger;
    /**
     * program settings
     */
    private Settings settings;

    /**
     * The initialization point of the program. Use "sync" to synchronize your xml file with the database
     * or use "export" to create xml file from the database.
     *
     * @param args Usage: jarfile [sync|export] filepath
     */

    public static void main(String[] args) {
        String usageString = "Usage: jarfile [sync|export] filepath";
        if (args.length != 2) {
            System.out.println(usageString);
            return;
        }
        DatabaseSync instance = new DatabaseSync();

        if (!instance.settings.isValid()) {
            System.out.println("Invalid config.");
        } else if (instance.logger == null) {
            System.out.println("Invalid logger.");
        } else
            switch (args[0]) {
                case "export":
                    instance.exportBase(args[1]);
                    break;
                case "sync":
                    instance.syncBase(args[1]);
                    break;
                default:
                    System.out.println(usageString);
            }
    }

    public DatabaseSync() {
        settings = new Settings();
        try {
            settings.load();
        } catch (IOException e) {
            System.err.println("Input/ouput error");
            return;
        }
        logger = settings.getLogger();
    }

    /**
     * Exports database records to the specified xml file.
     *
     * @param filePath xml file name
     */
    private void exportBase(String filePath) {
        logger.info("Export started");
        try (SQL sql = new SQL()) {
            logger.info("Connecting to SQL database...");
            sql.connect(settings.DBURL, settings.DBUSER, settings.DBPASS);
            sql.select();
            try (XML xml = new XML()) {
                logger.info("Constructing xml document...");
                xml.create(filePath);
                Job job = sql.next();
                while (job != null) {
                    xml.append(job);
                    job = sql.next();
                }
                logger.info("Writing xml document to the disk...");
                xml.write();
            }
            String logMessage = "Database was successfully exported";
            logger.info(logMessage);
            System.out.println(logMessage);
        } catch (SQLCustomException | XMLException e) {
            logger.error(e.getMessage());
            System.out.println("Export failed. See logs for more info.");
        }
    }

    /**
     * Synchronizes database with the specified xml file.
     *
     * @param filePath XML file name
     */
    private void syncBase(String filePath) {
        logger.info("Synchronization started");
        logger.info("Loading XML file...");
        JobSet jobs = new JobSet();
        try (XML xml = new XML()) {
            xml.open(filePath);
            Job job = xml.next();
            while (job != null) {
                if (!jobs.add(job)) {
                    throw new XMLException("XML file have two equal records!");
                }
                job = xml.next();
            }
        } catch (XMLException e) {
            logger.error(e.getMessage());
            System.out.println("Synchronizations failed. See logs for more info.");
            return;
        }
        if (jobs.size() == 0) {
            logger.info("XML file is empty! Database will be cleared");
        }

        try (SQL sql = new SQL()) {
            logger.info("Connecting to SQL database...");
            sql.connect(settings.DBURL, settings.DBUSER, settings.DBPASS);
            sql.select();
            Job job = sql.next();
            logger.info("Synchronizing...");
            while (job != null) {
                if (jobs.contains(job)) {
                    if (!job.getDescription().equals(jobs.getDescription(job))) {
                        logger.info("Updated: DepCode - " + job.getDepCode() + ", DepJob - " + job.getDepJob());
                        job.setDescription(jobs.getDescription(job));
                        sql.update(job);
                    }
                    jobs.remove(job);
                } else {
                    logger.info("Removed: DepCode - " + job.getDepCode() + ", DepJob - " + job.getDepJob());
                    sql.delete(job);
                }
                job = sql.next();
            }
            for (Job j : jobs) {
                logger.info("Added: DepCode - " + j.getDepCode() + ", DepJob - " + j.getDepJob());
                sql.insert(job);
            }
            sql.save();
            String logMessage = "Synchronization is done";
            logger.info(logMessage);
            System.out.println(logMessage);
        } catch (SQLCustomException e) {
            logger.error(e.getMessage());
            System.out.println("Synchronizations failed. See logs for more info.");

        }
    }
}
