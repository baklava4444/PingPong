package GamePack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/leaderBoard";
    private static final String username = "root";
    private static final String password = "1234";


    public static void saveMatchResult(String winner, String scoreText) {
        String insertSQL = "INSERT INTO match_results(winner, score) VALUES(?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL,username,password);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, winner);
            pstmt.setString(2, scoreText);
            pstmt.executeUpdate();
            System.out.println("Match saved successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to save match: " + e.getMessage());
        }
    }

    public static List<String> getLast10Results() {
        List<String> results = new ArrayList<>();
        String querySQL = "SELECT winner, score, match_date FROM match_results ORDER BY id DESC LIMIT 10";
        
        try (Connection conn = DriverManager.getConnection(URL,username,password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {
            
            while (rs.next()) {
                String record = String.format("Winner: %-6s | Score: %-5s", 
                        rs.getString("winner"), 
                        rs.getString("score"));
                results.add(record);
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch matches: " + e.getMessage());
        }
        return results;
    }
}
