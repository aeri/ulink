package urlshortener.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import urlshortener.domain.ShortURL;
import urlshortener.repository.ShortURLRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class ShortURLRepositoryImpl implements ShortURLRepository {

	private static final Logger log = LoggerFactory.getLogger(ShortURLRepositoryImpl.class);

	private static final RowMapper<ShortURL> rowMapper = (rs, rowNum) -> new ShortURL(rs.getString("hash"),
			rs.getString("target"), null, rs.getDate("created"), rs.getString("safe"), rs.getString("ip"),
			rs.getString("code"));

	private JdbcTemplate jdbc;

	public ShortURLRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public List<ShortURL> retrieveUrls(int limit, int offset) {
		try {
			return jdbc.query("SELECT * FROM shorturl LIMIT ? OFFSET ?", new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			return new ArrayList<ShortURL>();
		}
	}

	@Override
	public ShortURL findByKey(String id) {
		try {
			return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash=?", rowMapper, id);
		} catch (Exception e) {
			log.debug("When select for key {}", id, e);
			return null;
		}
	}

	@Override
	public ShortURL findByKeyCode(String id, String code) {
		try {
			return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash=? AND code=?", rowMapper, id, code);
		} catch (Exception e) {
			log.debug("When select for key {}", id, e);
			return null;
		}
	}

	@Override
	public ShortURL save(ShortURL su) {
		try {
			jdbc.update("INSERT INTO shorturl VALUES (?,?,DEFAULT,?,?,?)", su.getHash(), su.getTarget(), su.getSafe(),
					su.getIP(), su.getCode());
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
	public ShortURL mark(ShortURL su, String safeness) {
		try {
			jdbc.update("UPDATE shorturl SET safe=? WHERE hash=?", safeness, su.getHash());
			new DirectFieldAccessor(su).setPropertyValue("safe", safeness);
			return su;
		} catch (Exception e) {
			log.debug("When update", e);
			return null;
		}
	}

	@Override
	public void update(ShortURL su) {
		try {
			jdbc.update("update shorturl set target=?, created=?, safe=?, ip=?, code=? where hash=?", su.getTarget(),
					su.getCreated(), su.getSafe(), su.getIP(), su.getCode(), su.getHash());
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
			return jdbc.queryForObject("select count(*) from shorturl", Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}

	@Override
	public List<ShortURL> list(Long limit, Long offset) {
		try {
			return jdbc.query("SELECT * FROM shorturl LIMIT ? OFFSET ?", new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for limit {} and offset {}", limit, offset, e);
			return Collections.emptyList();
		}
	}

	@Override
	public List<ShortURL> findByTarget(String target) {
		try {
			return jdbc.query("SELECT * FROM shorturl WHERE target = ?", new Object[] { target }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for target " + target, e);
			return Collections.emptyList();
		}
	}
}