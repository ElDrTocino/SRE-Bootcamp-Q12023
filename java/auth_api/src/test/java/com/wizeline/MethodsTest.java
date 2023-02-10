package com.wizeline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.mockito.Mockito.mockStatic;

class MethodsTest {
    @Test
    void generateToken() {
        Assertions.assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4ifQ.StuYX978pQGnCeeaj2E1yBYwQvZIodyDTCJWXdsxBGI", Methods.generateToken("admin", "secret"));
    }

    @Test
    void accessData() {
        Assertions.assertEquals("You are under protected data", Methods.accessData("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4ifQ.StuYX978pQGnCeeaj2E1yBYwQvZIodyDTCJWXdsxBGI"));
    }

    @Test
    void generateToken_No_User() {
        Assertions.assertNull(Methods.generateToken("admin2", "secret2"));
    }

    @Test
    void accessData_Not_Valid_Token() {
        Assertions.assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", Methods.accessData("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4ifQ.StuYX978pQGnCeeaj2E1yBYwQvZIod23yDTCJWXdsxBGI"));
    }

    @Test()
    void generateToken_DB_Issue() throws SQLException {
        MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class);
        Mockito.when(DatabaseConnection.getInstance()).thenThrow(new SQLException());
        Assertions.assertThrows(RuntimeException.class, () -> Methods.generateToken("admin", "secret"));
        mockedStatic.close();
    }

    @Test()
    void accesData_DB_Issue() throws SQLException {
        MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class);
        Mockito.when(DatabaseConnection.getInstance()).thenThrow(new SQLException());
        Assertions.assertThrows(RuntimeException.class, () -> Methods.accessData("asdjfwoudajdhy"));
        mockedStatic.close();
    }




}