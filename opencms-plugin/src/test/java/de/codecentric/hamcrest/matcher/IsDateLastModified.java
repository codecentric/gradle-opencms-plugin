package de.codecentric.hamcrest.matcher;

import org.mockito.ArgumentMatcher;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class IsDateLastModified extends ArgumentMatcher<String> {

    public static final String END_TAG   = "</datelastmodified>";
    public static final String START_TAG = "<datelastmodified>";

    @Override
    public boolean matches( final Object argument ) {
        return argument instanceof String && isDateLastModified( (String) argument );
    }

    private boolean isDateLastModified( final String argument ) {
        return isDateLastModifiedTag( argument ) && isTimeStamp( removeTags( argument ) );
    }

    private boolean isTimeStamp( final String value ) {
        boolean match;
        DateFormat fmt = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss z" );
        try {
            fmt.parse( value );
            match = true;
        } catch( ParseException e ) {
            match = false;
        }
        return match;
    }

    private String removeTags( final String argument ) {
        return argument.substring( START_TAG.length(), getPresumedEndTagIndex( argument ) );
    }

    private boolean isDateLastModifiedTag( final String argument ) {
        int startTagIndex = argument.indexOf( START_TAG );
        int endTagIndex = argument.lastIndexOf( END_TAG );
        int presumedEndTagIndex = getPresumedEndTagIndex( argument );
        return startTagIndex == 0 && endTagIndex == presumedEndTagIndex;
    }

    private int getPresumedEndTagIndex( final String argument ) {
        return argument.length()-END_TAG.length();
    }
}
