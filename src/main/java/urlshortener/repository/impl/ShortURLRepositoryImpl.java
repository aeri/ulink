package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;

import java.util.Collections;
import java.util.List;

@Repository
public class ShortURLRepositoryImpl implements ShortURLRepository {

    private static final Logger log = LoggerFactory
            .getLogger(ShortURLRepositoryImpl.class);

    private static final RowMapper<ShortURL> rowMapper = (rs, rowNum) -> new ShortURL(rs.getString("hash"), rs.getString("target"),
            null, rs.getString("sponsor"), rs.getDate("created"),
            rs.getString("owner"), rs.getInt("mode"),
            rs.getBoolean("safe"), rs.getString("ip"),
            rs.getString("country"));

    private JdbcTemplate jdbc;

    public ShortURLRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public ShortURL findByKey(String id) {
        try {
            return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash=?",
                    rowMapper, id);
        } catch (Exception e) {
            log.debug("When select for key {}", id, e);
            return null;
        }
    }

    @Override
    public ShortURL save(ShortURL su) {
        try {
            jdbc.update("INSERT INTO shorturl VALUES (?,?,?,?,?,?,?,?,?)",
                    su.getHash(), su.getTarget(), su.getSponsor(),
                    su.getCreated(), su.getOwner(), su.getMode(), su.getSafe(),
                    su.getIP(), su.getCountry());
        } catch (DuplicateKeyException e) {
            log.debug("When insert for key {}", su.getHash(), e);
            return su;
        } catch (Exception e) {
            log.debug("When insert", e);
            return null;
        }
        return su;
    }

    @Override
    public ShortURL mark(ShortURL su, boolean safeness) {
        try {
            jdbc.update("UPDATE shorturl SET safe=? WHERE hash=?", safeness,
                    su.getHash());
            ShortURL res = new ShortURL();
            BeanUtils.copyProperties(su, res);
            new DirectFieldAccessor(res).setPropertyValue("safe", safeness);
            return res;
        } catch (Exception e) {
            log.debug("When update", e);
            return null;
        }
    }

    @Override
    public void update(ShortURL su) {
        try {
            jdbc.update(
                    "update shorturl set target=?, sponsor=?, created=?, owner=?, mode=?, safe=?, ip=?, country=? where hash=?",
                    su.getTarget(), su.getSponsor(), su.getCreated(),
                    su.getOwner(), su.getMode(), su.getSafe(), su.getIP(),
                    su.getCountry(), su.getHash());
        } catch (Exception e) {
            log.debug("When update for hash {}", su.getHash(), e);
        }
    }

    @Override
    public void delete(String hash) {
        try {
            jdbc.update("delete from shorturl where hash=?", hash);
        } catch (Exception e) {
            log.debug("When delete for hash {}", hash, e);
        }
    }

    @Override
    public Long count() {
        try {
            return jdbc.queryForObject("select count(*) from shorturl",
                    Long.class);
        } catch (Exception e) {
            log.debug("When counting", e);
        }
        return -1L;
    }

    @Override
    public List<ShortURL> list(Long limit, Long offset) {
        try {
            return jdbc.query("SELECT * FROM shorturl LIMIT ? OFFSET ?",
                    new Object[]{limit, offset}, rowMapper);
        } catch (Exception e) {
            log.debug("When select for limit {} and offset {}", limit, offset, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ShortURL> findByTarget(String target) {
        try {
            return jdbc.query("SELECT * FROM shorturl WHERE target = ?",
                    new Object[]{target}, rowMapper);
        } catch (Exception e) {
            log.debug("When select for target " + target, e);
            return Collections.emptyList();
        }
    }
}
