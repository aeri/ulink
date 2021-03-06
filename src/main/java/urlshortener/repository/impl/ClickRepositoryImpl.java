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

import urlshortener.domain.Browser;
import urlshortener.domain.Click;
import urlshortener.domain.Country;
import urlshortener.domain.Platform;
import urlshortener.repository.ClickRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Repository
public class ClickRepositoryImpl implements ClickRepository {

	private static final Logger log = LoggerFactory.getLogger(ClickRepositoryImpl.class);

	private static final RowMapper<Click> rowMapper = (rs, rowNum) -> new Click(rs.getLong("id"), rs.getString("hash"),
			rs.getDate("created"), rs.getString("browser"), rs.getString("platform"), rs.getString("ip"),
			rs.getString("country"), rs.getString("gc"), rs.getLong("latency"));

	private static final RowMapper<Country> coMapper = (rs, rowNum) -> new Country(rs.getString("gc"),
			rs.getString("country"), rs.getInt("count"));

	private static final RowMapper<Browser> boMapper = (rs, rowNum) -> new Browser(rs.getString("browser"),
			rs.getInt("count"));

	private static final RowMapper<Platform> ptMapper = (rs, rowNum) -> new Platform(rs.getString("platform"),
			rs.getInt("count"));

	private JdbcTemplate jdbc;

	public ClickRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	
	/** 
	 * Retrieves Click list (redirections) by shortened url
	 * 
	 * @param hash
	 * @return List<Click>
	 */
	@Override
	public List<Click> findByHash(String hash) {
		try {
			return jdbc.query("SELECT * FROM click WHERE hash=?", new Object[] { hash }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for hash " + hash, e);
			return Collections.emptyList();
		}
	}

	
	/** 
	 * Retrieves Country list (redirections) by shortened url
	 * 
	 * @param hash
	 * @return List<Country>
	 */
	@Override
	public List<Country> retrieveCountries(String hash) {
		log.debug("Retrieving countries: " + hash);
		try {
			return jdbc.query(
					"SELECT COUNT(id), country, gc FROM click WHERE hash=? GROUP BY country,gc ORDER BY COUNT(id) DESC",
					new Object[] { hash }, coMapper);
		} catch (Exception e) {
			log.debug("When select for key {}", hash, e);
			return null;
		}
	}

	
	/** 
	 * Retrieves Browser list (redirections) by shortened url
	 * 
	 * @param hash
	 * @return List<Browser>
	 */
	@Override
	public List<Browser> retrieveBrowsers(String hash) {
		log.debug("Retrieving browsers: " + hash);
		try {
			return jdbc.query(
					"SELECT COUNT(id), browser FROM click WHERE hash=? GROUP BY browser ORDER BY COUNT(id) DESC",
					new Object[] { hash }, boMapper);
		} catch (Exception e) {
			log.debug("When select for key {}", hash, e);
			return null;
		}
	}

	
	/** 
	 * Retrieves Platform list (redirections) by shortened url
	 * 
	 * @param hash
	 * @return List<Platform>
	 */
	@Override
	public List<Platform> retrievePlatforms(String hash) {
		log.debug("Retrieving platforms: " + hash);
		try {
			return jdbc.query(
					"SELECT COUNT(id), platform FROM click WHERE hash=? GROUP BY platform ORDER BY COUNT(id) DESC",
					new Object[] { hash }, ptMapper);
		} catch (Exception e) {
			log.debug("When select for key {}", hash, e);
			return null;
		}
	}

	
	/** 
	 * Retrieves Country list (all redirections)
	 * 
	 * @return List<Country>
	 */
	@Override
	public List<Country> retrieveCountriesGlobal() {
		try {
			return jdbc.query("SELECT COUNT(id), country, gc FROM click GROUP BY country,gc ORDER BY COUNT(id) DESC",
					coMapper);
		} catch (Exception e) {
			log.debug("When select for key {}", e);
			return null;
		}
	}

	
	/** 
	 * Retrieves Browser list (all redirections)
	 * 
	 * @return List<Browser>
	 */
	@Override
	public List<Browser> retrieveBrowsersGlobal() {
		try {
			return jdbc.query("SELECT COUNT(id), browser FROM click GROUP BY browser ORDER BY COUNT(id) DESC",
					boMapper);
		} catch (Exception e) {
			log.debug("When select for key {}", e);
			return null;
		}
	}

	
	/** 
	 * Retrieves Platform list (all redirections)
	 * 
	 * @return List<Platform>
	 */
	@Override
	public List<Platform> retrievePlatformsGlobal() {
		try {
			return jdbc.query("SELECT COUNT(id), platform FROM click GROUP BY platform ORDER BY COUNT(id) DESC",
					ptMapper);
		} catch (Exception e) {
			log.debug("When select for key {}", e);
			return null;
		}
	}

	
	/** 
	 * Stores Click (redirection) in DB
	 * 
	 * @param cl
	 * @return Click
	 */
	@Override
	public Click save(final Click cl) {
		try {
			KeyHolder holder = new GeneratedKeyHolder();
			jdbc.update(conn -> {
				PreparedStatement ps = conn.prepareStatement(
						"INSERT INTO CLICK VALUES (DEFAULT, ?, DEFAULT, ?, ?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, cl.getHash());
				ps.setString(2, cl.getBrowser());
				ps.setString(3, cl.getPlatform());
				ps.setString(4, cl.getIp());
				ps.setString(5, cl.getCountry());
				ps.setString(6, cl.getCountryCode());
				ps.setLong(7, cl.getLantency());
				return ps;
			}, holder);
			if ((Long)holder.getKeys().get("id") != null) {
				new DirectFieldAccessor(cl).setPropertyValue("id", (Long)holder.getKeys().get("id"));
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

	
	/** 
	 * Updates Click (redirection) in DB
	 * 
	 * @param cl
	 */
	@Override
	public void update(Click cl) {
		log.info("ID2: {} navegador: {} SO: {} Date: {}", cl.getId(), cl.getBrowser(), cl.getPlatform(),
				cl.getCreated());
		try {
			jdbc.update(
					"update click set hash=?, created=?, browser=?, platform=?, ip=?, country=?, countryCode=? where id=?",
					cl.getHash(), cl.getCreated(), cl.getBrowser(), cl.getPlatform(), cl.getIp(), cl.getCountry(),
					cl.getCountryCode(), cl.getId());

		} catch (Exception e) {
			log.info("When update for id " + cl.getId(), e);
		}
	}

	
	/** 
	 * Deletes Click (redirection) by ID in DB
	 * 
	 * @param id
	 */
	@Override
	public void delete(Long id) {
		try {
			jdbc.update("delete from click where id=?", id);
		} catch (Exception e) {
			log.debug("When delete for id " + id, e);
		}
	}

	/**
	 * Delete all clicks in database
	 * 
	 */
	@Override
	public void deleteAll() {
		try {
			jdbc.update("delete from click");
		} catch (Exception e) {
			log.debug("When delete all", e);
		}
	}

	
	/** 
	 * Returns number of clicks stored in DB
	 * 
	 * @return Long
	 */
	@Override
	public Long count() {
		try {
			return jdbc.queryForObject("select count(*) from click", Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}

	
	/** 
	 * Retrieves Click list given limit and offset
	 * 
	 * @param limit
	 * @param offset
	 * @return List<Click>
	 */
	@Override
	public List<Click> list(Long limit, Long offset) {
		try {
			return jdbc.query("SELECT * FROM click LIMIT ? OFFSET ?", new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for limit " + limit + " and offset " + offset, e);
			return Collections.emptyList();
		}
	}

	
	/** 
	 * Returns Click id by shortened url
	 * 
	 * @param hash
	 * @return Long
	 */
	@Override
	public Long clicksByHash(String hash) {
		try {
			return jdbc.queryForObject("select count(*) from click where hash = ?", new Object[] { hash }, Long.class);
		} catch (Exception e) {
			log.debug("When counting hash " + hash, e);
		}
		return -1L;
	}

	
	/** 
	 * Returns average latency in time range [since, now]
	 * 
	 * @param since
	 * @return Long
	 */
	@Override
	public Long retrieveAverageLatency(Timestamp since){
		try {
			return jdbc.queryForObject("SELECT AVG (LATENCY) FROM CLICK WHERE CREATED > ?", new Object[] { since }, Long.class);
		} catch (Exception e) {
			log.debug("When retrieving average latency since " + since, e);
		}
		return -1L;
	}

}