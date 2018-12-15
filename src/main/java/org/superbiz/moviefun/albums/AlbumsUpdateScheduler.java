package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateScheduler(DataSource dataSource, AlbumsUpdater albumsUpdater) {
        this.albumsUpdater = albumsUpdater;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 3 * MINUTES)
    public void run() {
        try {
            if (startAlbumScheduler()) {
                logger.debug("Starting albums update");
                albumsUpdater.update();
                logger.debug("Finished albums update");
            }
            else
                logger.debug("Nothing to start");

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }

    boolean startAlbumScheduler() {
//        long startTime = jdbcTemplate.query("SELECT started_at FROM album_scheduler_task ORDER BY started_at DESC LIMIT 1", resultSet -> {
//            if (resultSet.next())
//                return resultSet.getTimestamp(1);
//            else
//                return null;
//        }).getTime();
//
//        long currentTime = System.currentTimeMillis();
//
//        if (startTime < (currentTime-3) ) {
//            jdbcTemplate.update("INSERT INTO album_scheduler_task (started_at) VALUES (?)", new Timestamp(currentTime));
//            return true;
//        } else {
//            return false;
//        }
        int updatedRows = jdbcTemplate.update("UPDATE album_scheduler_task " +
                "SET started_at = now() " +
                " WHERE started_at IS NULL " +
                " OR started_at < date_sub(now(), INTERVAL 3 MINUTE)");
        return updatedRows > 0;
    }
}
