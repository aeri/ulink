package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import urlshortener.domain.Click;
import urlshortener.repository.ClickRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.List;


@Repository
public class ClickRepositoryImpl implements ClickRepository {

    private static final Logger log = LoggerFactory
            .getLogger(ClickRepositoryImpl.class);

    private static final RowMapper<Click> rowMapper = (rs, rowNum) -> new Click(rs.getLong("id"), rs.getString("hash"),
            rs.getDate("created"), rs.getString("referrer"),
            rs.getString("browser"), rs.getString("platform"),
            rs.getString("ip"), rs.getString("country"));

    private JdbcTemplate jdbc;

    public ClickRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Click> findByHash(String hash) {
        try {
            return jdbc.query("SELECT * FROM click WHERE hash=?",
                    new Object[]{hash}, rowMapper);
        } catch (Exception e) {
            log.debug("When select for hash " + hash, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Click save(final Click cl) {
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbc.update(conn -> {
                PreparedStatement ps = conn
                        .prepareStatement(
                                "INSERT INTO CLICK VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                ps.setNull(1, Types.BIGINT);
                ps.setString(2, cl.getHash());
                ps.setDate(3, cl.getCreated());
                ps.setString(4, cl.getReferrer());
                ps.setString(5, cl.getBrowser());
                ps.setString(6, cl.getPlatform());
                ps.setString(7, cl.getIp());
                ps.setString(8, cl.getCountry());
                return ps;
            }, holder);
            if (holder.getKey() != null) {
                new DirectFieldAccessor(cl).setPropertyValue("id", holder.getKey()
                        .longValue());
            } else {
                log.debug("Key from database is null");
            }
        } catch (DuplicateKeyException e) {
            log.debug("When insert for click with id " + cl.getId(), e);
            return cl;
        } catch (Exception e) {
            log.debug("When insert a click", e);
            return null;
        }
        return cl;
    }

    @Override
    public void update(Click cl) {
        log.info("ID2: {} navegador: {} SO: {} Date: {}", cl.getId(), cl.getBrowser(), cl.getPlatform(), cl.getCreated());
        try {
            jdbc.update(
                    "update click set hash=?, created=?, referrer=?, browser=?, platform=?, ip=?, country=? where id=?",
                    cl.getHash(), cl.getCreated(), cl.getReferrer(),
                    cl.getBrowser(), cl.getPlatform(), cl.getIp(),
                    cl.getCountry(), cl.getId());

        } catch (Exception e) {
            log.info("When update for id " + cl.getId(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            jdbc.update("delete from click where id=?", id);
        } catch (Exception e) {
            log.debug("When delete for id " + id, e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            jdbc.update("delete from click");
        } catch (Exception e) {
            log.debug("When delete all", e);
        }
    }

    @Override
    public Long count() {
        try {
            return jdbc
                    .queryForObject("select count(*) from click", Long.class);
        } catch (Exception e) {
            log.debug("When counting", e);
        }
        return -1L;
    }

    @Override
    public List<Click> list(Long limit, Long offset) {
        try {
            return jdbc.query("SELECT * FROM click LIMIT ? OFFSET ?",
                    new Object[]{limit, offset}, rowMapper);
        } catch (Exception e) {
            log.debug("When select for limit " + limit + " and offset "
                    + offset, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Long clicksByHash(String hash) {
        try {
            return jdbc
                    .queryForObject("select count(*) from click where hash = ?", new Object[]{hash}, Long.class);
        } catch (Exception e) {
            log.debug("When counting hash " + hash, e);
        }
        return -1L;
    }

}
