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

	
	/** 
	 * Retrieves ShortURL list given limit and offset
	 * 
	 * @param limit
	 * @param offset
	 * @return List<ShortURL>
	 */
	@Override
	public List<ShortURL> retrieveUrls(int limit, int offset) {
		try {
			return jdbc.query("SELECT * FROM shorturl LIMIT ? OFFSET ?", new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			return new ArrayList<ShortURL>();
		}
	}

	
	/** 
	 * Retrieves ShortURL by key
	 * 
	 * @param id
	 * @return ShortURL
	 */
	@Override
	public ShortURL findByKey(String id) {
		try {
			return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash=?", rowMapper, id);
		} catch (Exception e) {
			log.debug("When select for key {}", id, e);
			return null;
		}
	}

	
	/** 
	 * Retrieves ShortURL by key if code matches
	 * 
	 * @param id
	 * @param code
	 * @return ShortURL
	 */
	@Override
	public ShortURL findByKeyCode(String id, String code) {
		try {
			return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash=? AND code=?", rowMapper, id, code);
		} catch (Exception e) {
			log.debug("When select for key {}", id, e);
			return null;
		}
	}

	
	/** 
	 * Save shortened url in database
	 * 
	 * @param su
	 * @return ShortURL
	 */
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

	
	/** 
	 * Change shortened url safeness property in database
	 * 
	 * @param su
	 * @param safeness
	 * @return ShortURL
	 */
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

	
	/** 
	 * Update shortened url in database
	 * 
	 * @param su
	 */
	@Override
	public void update(ShortURL su) {
		try {
			jdbc.update("update shorturl set target=?, created=?, safe=?, ip=?, code=? where hash=?", su.getTarget(),
					su.getCreated(), su.getSafe(), su.getIP(), su.getCode(), su.getHash());
		} catch (Exception e) {
			log.debug("When update for hash {}", su.getHash(), e);
		}
	}

	
	/** 
	 * Delete ShortURL by its shortened url
	 * 
	 * @param hash
	 */
	@Override
	public void delete(String hash) {
		try {
			jdbc.update("delete from shorturl where hash=?", hash);
		} catch (Exception e) {
			log.debug("When delete for hash {}", hash, e);
		}
	}

	
	/** 
	 * Returns number of shortened urls stored in DB
	 * 
	 * @return Long
	 */
	@Override
	public Long count() {
		try {
			return jdbc.queryForObject("select count(*) from shorturl", Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}

	
	/** 
	 * Retrieve list of ShortURL by target
	 * 
	 * @param target
	 * @return List<ShortURL>
	 */
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