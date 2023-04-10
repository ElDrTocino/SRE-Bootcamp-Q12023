package com.wizeline;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Methods {
  public static String generateToken(String username, String password) {
    String jws = null;
    SecretKeySpec secretKey = getSecretKey();

    try {
      Optional<User> user = getUserFromDB(username);
      if(user.isPresent() && validateUser(password, user.get())){
        jws = Jwts.builder()
                .setHeaderParam("alg","HS256")
                .setHeaderParam("typ","JWT")
                .setPayload("{\"role\":\""+user.get().getRole()+"\"}")
                .signWith(secretKey)
                .compact();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
    return jws;
  }
  public static String accessData(String authorization){
    String response;
    SecretKeySpec secretKey = getSecretKey();
    String token = authorization.replace("Bearer ","");
    try{
      Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
      response = validateRole(String.valueOf(claims.get("role"))) ? "You are under protected data" : "JWT validity cannot be asserted and should not be trusted";
    }catch(SignatureException ex){
      response = ex.getMessage();
    }
    return response;
  }

  private static boolean validateRole(String role)  {
    List<String> roles;
    try {
      roles = getRoles();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return roles.stream().anyMatch(r -> r.equals(role));

  }

  private static Optional<User> getUserFromDB(String username) throws SQLException {
    System.out.println("LogTestMentoria");
    System.out.println("LogTestMentoria");
    Optional<User> user = Optional.empty();
    Connection connection = DatabaseConnection.getInstance().getConnection();
    String query = "SELECT * FROM users WHERE username = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1,username);
    ResultSet rs = preparedStatement.executeQuery();
    if(rs.next()){
      user = Optional.of(new User(rs.getString("username"),rs.getString("password"),rs.getString("salt"),rs.getString("role")));
    }
    return user;
  }

  private static List<String> getRoles() throws SQLException {
    List<String> roles = new ArrayList<>();
    Connection connection = DatabaseConnection.getInstance().getConnection();
    String query = "SELECT role FROM users";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    ResultSet rs = preparedStatement.executeQuery();
    while(rs.next()){
      roles.add(rs.getString("role"));
    }
    return roles;
  }

  private static boolean validateUser(String password, User user){
    String dbPassword = user.getPassword();
    String shaPwd = getSHA512(password+user.getSalt());
    return dbPassword.equals(shaPwd);

  }

  private static String getSHA512(String input){

    String toReturn = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-512");
      digest.reset();
      digest.update(input.getBytes("utf8"));
      toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return toReturn;
  }

  private static SecretKeySpec getSecretKey(){
    String key = "my2w7wjd7yXF64FIADfJxNs1oupTGAuW";
    return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
  }
}

