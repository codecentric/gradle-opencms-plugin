package de.codecentric.hamcrest.matcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsDateLastModifiedTest {
    IsDateLastModified matcher;
    @Before
    public void setUp() throws Exception {
        matcher = new IsDateLastModified();
    }

    @Test
    public void whenMatchingNull_shouldReturnFalse() throws Exception {
        assertFalse( matcher.matches( null ) );
    }

    @Test
    public void whenMatchingEmptyString_shouldReturnFalse() throws Exception {
        assertFalse( matcher.matches( "" ) );
    }

    @Test
    public void whenMatchingArbitraryText_shouldReturnFalse() throws Exception {
        assertFalse( matcher.matches( "sometext" ) );
    }

    @Test
    public void whenMatchingDateLastModifiedTagWithInvalidValue_shouldReturnFalse() throws Exception {
        assertFalse( matcher.matches( "<datelastmodified></datelastmodified>" ) );
        assertFalse( matcher.matches( "<datelastmodified>asdf</datelastmodified>" ) );
        assertFalse( matcher.matches( "<datelastmodified><sometag /></datelastmodified>" ) );
        assertFalse( matcher.matches( "<datelastmodified>162367489364</datelastmodified>" ) );
        assertFalse( matcher.matches( "<datelastmodified>Tue: </datelastmodified>" ) );
    }

    @Test
    public void whenMatchingDateLastModifiedTagWithValidValue_shouldReturnTrue() throws Exception {
        // Valid Format: "EEE, dd MMM yyyy HH:mm:ss z"
        DateFormat fmt = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss z" );
        String date = fmt.format( new Date() );
        assertTrue( matcher.matches( "<datelastmodified>"+date+"</datelastmodified>" ) );
    }

    @After
    public void tearDown() throws Exception {
    }
} 