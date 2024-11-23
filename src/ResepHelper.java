import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class ResepHelper {
    
    
    
    
    public static Connection KoneksiDatabase() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:E:\\Keluargaku\\Muhammad Ihsan\\Kuliah\\PBO2\\UTS\\DatabaseResepMakanan.db");
            System.out.println("Koneksi berhasil!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver SQLite tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }

    public static void MembuatTabelResep() {
    String sql = "CREATE TABLE IF NOT EXISTS resep (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " nama_resep TEXT NOT NULL,\n"
                + " bahan_bahan TEXT NOT NULL,\n"
                + " langkah_langkah TEXT NOT NULL,\n"
                + " kategori TEXT NOT NULL\n"
                + ");";
    try (Connection conn = KoneksiDatabase();
         Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

    
    public static void MenambahResep(String nama_resep, String bahan_bahan, String langkah_langkah, String kategori) {
        String sql = "INSERT INTO resep(nama_resep, bahan_bahan, langkah_langkah, kategori) VALUES(?, ?, ?, ?)";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama_resep);
            pstmt.setString(2, bahan_bahan);
            pstmt.setString(3, langkah_langkah);
            pstmt.setString(4, kategori);
            pstmt.executeUpdate();
            System.out.println("Resep berhasil ditambahkan.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Map<String, String>> DapatkanResep() {
        List<Map<String, String>> nama_resep = new ArrayList<>();
        String sql = "SELECT * FROM resep";

        try (Connection conn = KoneksiDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, String> dataResep = new HashMap<>();
                dataResep.put("id", String.valueOf(rs.getInt("id")));
                dataResep.put("nama_resep", rs.getString("nama_resep"));
                dataResep.put("bahan_bahan", rs.getString("bahan_bahan"));
                dataResep.put("langkah_langkah", rs.getString("langkah_langkah"));
                dataResep.put("kategori", rs.getString("kategori"));
                nama_resep.add(dataResep);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nama_resep;
    }

    public static void MemperbaruiResep(int id, String nama_resep, String bahan_bahan, String langkah_langkah, String kategori) {
        String sql = "UPDATE resep SET nama_resep = ?, bahan_bahan = ?, langkah_langkah = ?, kategori = ? WHERE id = ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama_resep);
            pstmt.setString(2, bahan_bahan);
            pstmt.setString(3, langkah_langkah);
            pstmt.setString(4, kategori);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            System.out.println("Resep berhasil diperbarui.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void MenghapusResep(int id) {
        String sql = "DELETE FROM resep WHERE id = ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Resep berhasil dihapus.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static List<Map<String, String>> MencariResep(String keyword) {
    List<Map<String, String>> ListResep = new ArrayList<>();
    String sql = "SELECT * FROM resep WHERE nama_resep LIKE ?";

        try (Connection conn = KoneksiDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Gunakan '%' untuk pencarian mirip (LIKE)
            pstmt.setString(1, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, String> resep = new HashMap<>();
                resep.put("id", String.valueOf(rs.getInt("id")));
                resep.put("nama_resep", rs.getString("nama_resep"));
                resep.put("bahan_bahan", rs.getString("bahan_bahan"));
                resep.put("langkah_langkah", rs.getString("langkah_langkah"));
                resep.put("kategori", rs.getString("kategori"));
                ListResep.add(resep);
            }
        } catch (SQLException e) {
            System.out.println("Error saat mencari kontak: " + e.getMessage());
        }
        return ListResep;
    }

    
    
    
   


    
    
    
    // Mendapatkan resep makanan secara baris per baris
    public void getResepBarisPerBaris(String query, JTextArea txtAreaResep, Supplier<Boolean> shouldStop) {
        
    try {
        String apiKey = "a99ad06c83bc4661a7d508abe178b999";
        String searchUrl = "https://api.spoonacular.com/recipes/complexSearch?apiKey=" + apiKey + "&query=" + query;
        URL url = new URL(searchUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (shouldStop.get()) {
                in.close();
                conn.disconnect();
                SwingUtilities.invokeLater(() ->
                        txtAreaResep.setText("Pengambilan data dibatalkan.\n"));
                return;
            }
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        JSONObject json = new JSONObject(content.toString());
        JSONArray results = json.getJSONArray("results");

        if (results.length() == 0) {
            SwingUtilities.invokeLater(() ->
                    txtAreaResep.setText("Tidak ada resep yang ditemukan untuk pencarian ini."));
            return;
        }

        for (int i = 0; i < results.length(); i++) {
            if (shouldStop.get()) {
                SwingUtilities.invokeLater(() ->
                        txtAreaResep.setText("Pengambilan data dibatalkan.\n"));
                return;
            }

            JSONObject recipe = results.getJSONObject(i);
            int recipeId = recipe.getInt("id");
            String detailUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=" + apiKey;
            URL detailApiUrl = new URL(detailUrl);
            HttpURLConnection detailConn = (HttpURLConnection) detailApiUrl.openConnection();
            detailConn.setRequestMethod("GET");
            detailConn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int detailResponseCode = detailConn.getResponseCode();
            if (detailResponseCode != 200) {
                throw new Exception("Gagal mendapatkan detail resep dengan ID: " + recipeId);
            }

            BufferedReader detailReader = new BufferedReader(new InputStreamReader(detailConn.getInputStream()));
            StringBuilder detailContent = new StringBuilder();
            while ((inputLine = detailReader.readLine()) != null) {
                if (shouldStop.get()) {
                    detailReader.close();
                    detailConn.disconnect();
                    SwingUtilities.invokeLater(() ->
                            txtAreaResep.setText("Pengambilan data dibatalkan.\n"));
                    return;
                }
                detailContent.append(inputLine);
            }
            detailReader.close();
            detailConn.disconnect();

            JSONObject recipeDetail = new JSONObject(detailContent.toString());
            String title = recipeDetail.getString("title");

            // Terjemahkan judul
            String translatedTitle = translateToIndonesian(title);

            // Ambil bahan-bahan dan terjemahkan
            JSONArray ingredients = recipeDetail.getJSONArray("extendedIngredients");
            StringBuilder bahanBuilder = new StringBuilder();
            for (int j = 0; j < ingredients.length(); j++) {
                JSONObject ingredient = ingredients.getJSONObject(j);
                String bahan = ingredient.getString("original");
                String translatedBahan = translateToIndonesian(bahan);
                bahanBuilder.append("- ").append(translatedBahan).append("\n");
            }

            // Ambil langkah-langkah dan terjemahkan
            JSONArray stepsArray = recipeDetail.getJSONArray("analyzedInstructions").getJSONObject(0).getJSONArray("steps");
            StringBuilder langkahBuilder = new StringBuilder();
            for (int k = 0; k < stepsArray.length(); k++) {
                JSONObject step = stepsArray.getJSONObject(k);
                String langkah = step.getString("step");
                String translatedLangkah = translateToIndonesian(langkah);
                langkahBuilder.append(k + 1).append(". ").append(translatedLangkah).append("\n");
            }

            // Tampilkan hasil di JTextArea
            String hasilResep = "Resep: " + translatedTitle + "\n\n"
                    + "Bahan-Bahan:\n" + bahanBuilder.toString() + "\n"
                    + "Langkah-Langkah:\n" + langkahBuilder.toString() + "\n\n";

            SwingUtilities.invokeLater(() ->
                    txtAreaResep.append(hasilResep));
        }
    } catch (Exception e) {
        SwingUtilities.invokeLater(() ->
                txtAreaResep.setText("Gagal mendapatkan data resep: " + e.getMessage()));
    }


    }
    
    

    
    


    

    
    private String translateToIndonesian(String text) {
    try {
        String urlString = "https://lingva.ml/api/v1/en/id/" + text.replace(" ", "%20");
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP response code: " + responseCode);
        }

        StringBuilder content;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        conn.disconnect();

        System.out.println("Response from API: " + content.toString()); // Debug output

        JSONObject json = new JSONObject(content.toString());
        return json.getString("translation");
    } catch (Exception e) {
        e.printStackTrace(); // Debug error
        return text + " (Gagal diterjemahkan)";
    }
}


}
