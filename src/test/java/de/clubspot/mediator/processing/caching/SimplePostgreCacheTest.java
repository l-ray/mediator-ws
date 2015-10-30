package de.clubspot.mediator.processing.caching;

import org.apache.cocoon.pipeline.caching.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimplePostgreCacheTest {

    @Mock
    private Connection _mockedDb;

    private SimplePostgreCache _underTest;

    @Before
    public void setUp() throws Exception {
        _underTest = new SimplePostgreCache(_mockedDb);
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertThat(
                "toString contains class Name",
                _underTest.toString(),
                contains(_underTest.getClass().getSimpleName())
        );
    }

    @Test
    public void testRetrieve() throws Exception {

        String testContent = "test§*äüö !\"";

        PreparedStatement statement = mockPreparedStatement(testContent);

        CacheKey key = new SimpleCacheKey();
        CacheValue value = new CompleteCacheValue(testContent.getBytes(),key);

        final CacheValue actual = _underTest.retrieve(key);

        verify(statement, times(1)).setInt(anyInt(), eq(key.hashCode()));
        verify(statement, times(1)).executeQuery();
        Assert.assertEquals(
                new String((byte[])value.getValue()),
                new String((byte[])actual.getValue()));
    }

    @Test
    public void testRetrieveForEmpty() throws Exception {
        String testContent = "test§*äüö !\"";

        mockPreparedStatement(testContent,0);

        CacheKey key = new SimpleCacheKey();

        Assert.assertNull(_underTest.retrieve(key));
    }

    @Test
    public void testStoreForInsert() throws Exception {

        String testContent = "test§*äüö !\"";

        final boolean asUpdate = false;

        PreparedStatement statement = mockPreparedStatement(testContent, asUpdate);

        CacheKey key = new SimpleCacheKey();
        CacheValue value = new CompleteCacheValue(testContent.getBytes(),key);

        _underTest.store(key, value);

        Assert.assertEquals("Number of keys in keyset is.", 1, _underTest.keySet().size());
        Assert.assertTrue("Key is part of keyset.",_underTest.keySet().contains(key));

        verify(_mockedDb, times(1)).prepareStatement(Mockito.contains("SELECT"));
        verify(statement, times(1)).executeQuery();

        verify(_mockedDb, times(1)).prepareStatement(Mockito.contains("INSERT"));
        // once for select, once for insert
        verify(statement, times(2)).setInt(anyInt(), eq(key.hashCode()));
        verify(statement, times(1)).execute();

        verify(_mockedDb, never()).prepareStatement(Mockito.contains("UPDATE"));
    }

    @Test
    public void testStoreForUpdate() throws Exception {
        String testContent = "test§*äüö !\"";

        PreparedStatement statement = mockPreparedStatement(testContent);

        CacheKey key = new SimpleCacheKey();
        CacheValue value = new CompleteCacheValue(testContent.getBytes(),key);

        _underTest.store(key, value);

        Assert.assertEquals("Number of keys in keyset is.",1, _underTest.keySet().size());
        Assert.assertTrue("Key is part of keyset.",_underTest.keySet().contains(key));

        verify(_mockedDb, times(1)).prepareStatement(Mockito.contains("SELECT"));
        verify(statement, times(1)).executeQuery();

        verify(_mockedDb, never()).prepareStatement(Mockito.contains("INSERT"));
        verify(statement, never()).execute();
        // once for select, once for insert


        verify(_mockedDb, times(1)).prepareStatement(Mockito.contains("UPDATE"));
        verify(statement, times(2)).setInt(anyInt(), eq(key.hashCode()));
    }


    @Test
    public void testDoClear() throws Exception {

        mockPreparedStatement("dummy");

        CacheKey key1 = new SimpleCacheKey();
        CacheValue v1 = new CompleteCacheValue("test1".getBytes(), key1);

        _underTest.store(key1, v1);

        Assert.assertEquals(1, _underTest.retrieveKeySet().size());

        _underTest.doClear();

        Assert.assertEquals(0, _underTest.retrieveKeySet().size());
    }


    @Test
    public void removesSingleKeyAsOnlyKey() throws Exception {
        mockPreparedStatement("dummy");

        CacheKey key1 = new SimpleCacheKey();
        CacheValue v1 = new CompleteCacheValue("test1".getBytes(), key1);

        _underTest.store(key1, v1);

        Assert.assertEquals(1, _underTest.retrieveKeySet().size());

        _underTest.doRemove(key1);

        Assert.assertEquals(0, _underTest.retrieveKeySet().size());
    }

    @Test
    public void removesSingleKeyFromMultipleKeys() throws Exception {
        mockPreparedStatement("dummy");

        CacheKey key1 = new SimpleCacheKey();
        CacheValue v1 = new CompleteCacheValue("test1".getBytes(), key1);

        CacheKey key2 = new ExpiresCacheKey(new SimpleCacheKey(),"500");
        CacheValue v2 = new CompleteCacheValue("test2".getBytes(), key2);

        _underTest.store(key1, v1);
        _underTest.store(key2, v2);

        Assert.assertEquals(2, _underTest.retrieveKeySet().size());

        _underTest.doRemove(key1);

        Assert.assertEquals(1, _underTest.retrieveKeySet().size());
    }

    @Test
    public void testRetrieveKeySet() throws Exception {
        mockPreparedStatement("dummy");

        CacheKey key1 = new SimpleCacheKey();
        CacheValue v1 = new CompleteCacheValue("test1".getBytes(), key1);

        CacheKey key2 = new ExpiresCacheKey(new SimpleCacheKey(),"500");
        CacheValue v2 = new CompleteCacheValue("test2".getBytes(), key2);

        Assert.assertEquals(0, _underTest.retrieveKeySet().size());

        _underTest.store(key1, v1);

        Assert.assertEquals(1, _underTest.retrieveKeySet().size());

        _underTest.store(key2, v2);

        Assert.assertEquals(2, _underTest.retrieveKeySet().size());

        Assert.assertTrue(_underTest.retrieveKeySet().contains(key1));
        Assert.assertTrue(_underTest.retrieveKeySet().contains(key2));

    }

    private PreparedStatement mockPreparedStatement(String mockedResultsetString) throws SQLException  {
        return mockPreparedStatement(mockedResultsetString, 1, true);
    }

    private PreparedStatement mockPreparedStatement(String mockedResultsetString, int resultCount) throws SQLException  {
        return mockPreparedStatement(mockedResultsetString, resultCount, true);
    }

    private PreparedStatement mockPreparedStatement(String mockedResultsetString, boolean asUpdate) throws SQLException  {
        return mockPreparedStatement(mockedResultsetString, 1, asUpdate);
    }

    private PreparedStatement mockPreparedStatement(String mockedResultsetString, int resultCount, boolean asInsert) throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);

        ResultSet resultSet = mock(ResultSet.class);

        if (resultCount == 0 ) {
            when(resultSet.next()).thenReturn(false);
        }

        if (resultCount == 1 ) {
            when(resultSet.next()).thenReturn(true).thenReturn(false);
        }

        when(resultSet.getInt(anyString())).thenReturn(asInsert ? 1 : 0 );
        when(resultSet.getBytes(anyString())).thenReturn(mockedResultsetString.getBytes());

        when(statement.executeQuery()).thenReturn(resultSet);
        when(_mockedDb.prepareStatement(anyString())).thenReturn(statement);


        return statement;
    }

    private static Matcher contains(final Object expected){

        return new BaseMatcher() {

            protected Object theExpected = expected;

            public boolean matches(Object o) {
                return ((String) o).contains((String)theExpected);
            }

            public void describeTo(Description description) {
                description.appendText(theExpected.toString());
            }
        };
    }
}