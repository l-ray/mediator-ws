package de.clubspot.mediator.processing.caching;

import org.apache.cocoon.pipeline.ProcessingException;
import org.apache.cocoon.pipeline.caching.*;
import org.apache.cocoon.pipeline.util.StringRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * A very simple implementation of the {@link Cache} interface using a postgresql database.
 */
public class SimplePostgreCache extends AbstractCache {

    private static final Logger LOG =
            LoggerFactory.getLogger(SimplePostgreCache.class.getName());
    public static final String INSERT_INTO_CACHE_HASH_CODE_KEY_VALUE_VALUES = "INSERT INTO cache (hashCode, value) VALUES (?, ?)";
    public static final String UPDATE_CACHE_HASH_CODE_KEY_VALUE_VALUES = "UPDATE cache SET value=? where hashCode= ?";

    public static final String SELECT_VALUE_FROM_CACHE_WHERE_HASH_CODE = "SELECT * FROM cache WHERE hashCode = ?";
    public static final String DELETE_FROM_CACHE_AS_C_WHERE_C_HASH_CODE = "DELETE FROM cache AS c WHERE c.hashCode= ?";
    public static final String DELETE_ALL_FROM_CACHE = "DELETE * FROM cache";
    public static final String SELECT_COUNT_FROM_CACHE = "SELECT count(*) as hcount from cache WHERE hashCode=?";

    private Connection dbConnection;

    private final Set<CacheKey> map = new HashSet<>();

    public SimplePostgreCache(Connection dbConnection) {
        assert(dbConnection != null);
        this.dbConnection = dbConnection;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return StringRepresentation.buildString(this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.pipeline.caching.AbstractCache#retrieve(org.apache.cocoon.pipeline.caching.CacheKey)
     */
    @Override
    protected CacheValue retrieve(final CacheKey cacheKey) {
        /*
        synchronized (this.map) {
            if (! this.map.contains(cacheKey)) {
                LOG.trace("CacheKey "+cacheKey.toString()+" does not exist yet");
                return null;
            }*/
        LOG.trace("returning value from cache for cachekey "+ cacheKey.toString());
        return retrieveFromDatabase(cacheKey);
        /*}*/
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.pipeline.caching.AbstractCache#store(org.apache.cocoon.pipeline.caching.CacheKey,
     * org.apache.cocoon.pipeline.caching.CacheValue)
     */
    @Override
    protected void store(final CacheKey cacheKey, final CacheValue cacheValue) {
        boolean freshValue;
        synchronized (this.map) {
            freshValue = this.map.add(cacheKey);
        }
        if (freshValue) {
            LOG.trace("persisting new cache value");
            storeInDatabase(cacheKey, cacheValue);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.pipeline.caching.AbstractCache#doClear()
     */
    @Override
    protected void doClear() {
        synchronized (this.map) {
            this.map.clear();
        }
        clearDatabase();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.pipeline.caching.AbstractCache#doRemove(org.apache.cocoon.pipeline.caching.CacheKey)
     */
    @Override
    protected boolean doRemove(final CacheKey key) {
        boolean removed;

        synchronized (this.map) {
            removed = this.map.remove(key);
        }

        return removed && removeFromDatabase(key);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.pipeline.caching.AbstractCache#retrieveKeySet()
     */
    @Override
    protected Set<CacheKey> retrieveKeySet() {
        synchronized (this.map) {
            return this.map;
        }
    }

     private boolean storeInDatabase(CacheKey key,CacheValue value) {

        boolean success;

        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            value.writeTo(baos);
            LOG.debug("Caching object "+value.toString()+" with content "+baos.size());
            baos.close();

            PreparedStatement checkExistence = prepareStatement(SELECT_COUNT_FROM_CACHE);
            checkExistence.setInt(1,key.hashCode());
            ResultSet rsExistence = checkExistence.executeQuery();
            rsExistence.next();
            PreparedStatement ps;

            if (rsExistence.getInt("hcount")==0) {
                ps = prepareStatement(INSERT_INTO_CACHE_HASH_CODE_KEY_VALUE_VALUES);
                ps.setInt(1, key.hashCode());
                ps.setBytes(2, baos.toByteArray());
                success = ps.execute();
            } else {
                ps = prepareStatement(UPDATE_CACHE_HASH_CODE_KEY_VALUE_VALUES);
                ps.setBytes(1, baos.toByteArray());
                ps.setInt(2, key.hashCode());
                success = ps.executeUpdate() > 0;
            }
            ps.close();

        } catch (SQLException | IOException e) {
            throw new ProcessingException(e);
        }
        return success;
    }

    private CacheValue retrieveFromDatabase(CacheKey key) {

        CacheValue value;

        try {

            PreparedStatement ps = prepareStatement(SELECT_VALUE_FROM_CACHE_WHERE_HASH_CODE);

            ps.setInt(1, key.hashCode());
            ResultSet rs = ps.executeQuery();
            rs.next();

            value = new CompleteCacheValue(
                    rs.getBytes("value"),
                    key
            );

            ps.close();

        } catch (SQLException e) {
            throw new ProcessingException(e);
        }
        return value;
    }

    private boolean removeFromDatabase(CacheKey cacheKey) {

        boolean removed;

        try {
            PreparedStatement stmt = prepareStatement(DELETE_FROM_CACHE_AS_C_WHERE_C_HASH_CODE);
            stmt.setInt(1,cacheKey.hashCode());

            removed = stmt.execute();

            stmt.close();

        } catch (SQLException e) {
            throw new ProcessingException(e);
        }

        return removed;
    }

    private void clearDatabase() {

        try {
            PreparedStatement stmt = prepareStatement(DELETE_ALL_FROM_CACHE);
            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            throw new ProcessingException(e);
        }
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException {
        Connection c = this.dbConnection;
        return c.prepareStatement(sql);
    }

}
